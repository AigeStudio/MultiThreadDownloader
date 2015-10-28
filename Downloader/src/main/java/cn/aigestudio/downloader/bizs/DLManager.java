package cn.aigestudio.downloader.bizs;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.aigestudio.downloader.interfaces.IDListener;

import static cn.aigestudio.downloader.bizs.DLCons.DEBUG;
import static cn.aigestudio.downloader.bizs.DLError.ERROR_CANNOT_GET_URL;
import static cn.aigestudio.downloader.bizs.DLError.ERROR_CREATE_FILE;
import static cn.aigestudio.downloader.bizs.DLError.ERROR_INVALID_URL;
import static cn.aigestudio.downloader.bizs.DLError.ERROR_NOT_NETWORK;
import static cn.aigestudio.downloader.bizs.DLError.ERROR_OPEN_CONNECT;
import static cn.aigestudio.downloader.bizs.DLError.ERROR_REPEAT_URL;
import static java.net.HttpURLConnection.HTTP_MOVED_PERM;
import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_NOT_MODIFIED;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_PARTIAL;
import static java.net.HttpURLConnection.HTTP_SEE_OTHER;

/**
 * 下载管理器
 * Download manager
 * 执行具体的下载操作
 *
 * @author AigeStudio 2015-05-09
 *         开始一个下载任务只需调用{@link #dlStart}方法即可
 *         停止某个下载任务需要调用{@link #dlStop}方法 停止下载任务仅仅会将对应下载任务移除下载队列而不删除相应数据 下次启动相同任务时会自动根据上一次停止时保存的数据重新开始下载
 *         取消某个下载任务需要调用{@link #dlCancel}方法 取消下载任务会删除掉相应的本地数据库数据但文件不会被删除
 *         相同url的下载任务视为相同任务
 *         Use {@link #dlStart} for a new download task.
 *         Use {@link #dlStop} to stop a download task base on url.
 *         Use {@link #dlCancel} to cancel a download task base on url.
 *         By the way, the difference between {@link #dlStop} and {@link #dlCancel} is whether the data in database would be deleted or not,
 *         for example, the state of download like local file and data in database will be save when you use {@link #dlStop} stop a download task,
 *         if you use {@link #dlCancel} cancel a download task, anything related to download task would be deleted.
 * @author AigeStudio 2015-05-26
 *         对不支持断点下载的文件直接使用单线程下载 该操作将不会插入数据库
 *         对转向地址进行解析
 *         更改下载线程分配逻辑
 *         DLManager will download with single thread if server does not support break-point, and it will not insert to database
 *         Support url redirection.
 *         Change download thread size dispath.
 * @author AigeStudio 2015-05-29
 *         修改域名重定向后无法多线程下载问题
 *         修改域名重定向后无法暂停问题
 *         Bugfix:can not start multi-threads to download file when we in url redirection.
 *         Bugfix:can not stop a download task when we in url redirection.
 * @author zhangchi 2015-10-13
 *         Bugfix：修改多次触发任务时的并发问题，防止同时触发多个相同的下载任务；修改任务队列为线程安全模式；
 *         修改多线程任务的线程数量设置机制，每个任务可以自定义设置下载线程数量；通过同构方法dlStart(String url, String dirPath, DLTaskListener listener,int threadNum)；
 *         添加日志开关及日志记录，开关方法为setDebugEnable，日志TAG为DLManager；方便调试;
 * @author AigeStudio 2015-10-23
 *         修复大部分已知Bug
 *         优化代码逻辑适应更多不同的下载情况
 *         完善错误码机制，使用不同的错误码标识不同错误的发生，详情请参见{@link DLError}
 *         不再判断网络类型只会对是否联网做一个简单的判断
 *         修改{@link #setDebugEnable(boolean)}方法
 *         新增多个不同的{@link #dlStart}方法便于回调
 *         新增{@link #setMaxTask(int)}方法限制多个下载任务的并发数
 */
public final class DLManager {
    private static final String TAG = DLManager.class.getSimpleName();

    private static final int CORES = Runtime.getRuntime().availableProcessors() * 2;

    private static final int HTTP_TEMP_REDIRECT = 307;
    private static final int HTTP_REQUESTED_RANGE_NOT_SATISFIABLE = 416;

    private static final int LENGTH_PER_THREAD = 4194304;

    private static DLManager sManager;
    private static DBManager sDBManager;

    private final ExecutorService mPreExecutor, mTaskExecutor;
    private Context context;

    private final List<String> URL_DLING = Collections.synchronizedList(new ArrayList<String>());
    private final List<Info> TASK_PREPARE = Collections.synchronizedList(new ArrayList<Info>());
    private final ConcurrentHashMap<String, DLTask> TASK_DLING = new ConcurrentHashMap<>();

    private int maxTask = Integer.MAX_VALUE;

    private DLManager(Context context) {
        this.context = context;
        mPreExecutor = Executors.newSingleThreadExecutor();
        mTaskExecutor = new ThreadPoolExecutor(CORES, CORES, 1, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
        sDBManager = DBManager.getInstance(context);
    }

    public static DLManager getInstance(Context context) {
        if (null == sManager) {
            sManager = new DLManager(context);
        }
        return sManager;
    }

    /**
     * 设置并发下载任务最大值
     * The max task of DLManager.
     *
     * @param maxTask ...
     * @return ...
     */
    public DLManager setMaxTask(int maxTask) {
        this.maxTask = maxTask;
        return sManager;
    }

    /**
     * 设置是否开启Debug模式 默认不开启
     * Is debug mode, default is false.
     *
     * @param isDebug ...
     * @return ...
     */
    public DLManager setDebugEnable(boolean isDebug) {
        DLCons.DEBUG = isDebug;
        return sManager;
    }

    /**
     * 根据Url暂停一个下载任务
     * Stop a download task base on url.
     *
     * @param url 文件下载地址
     *            Download url.
     */
    public void dlStop(String url) {
        if (TASK_DLING.containsKey(url)) {
            DLTask task = TASK_DLING.get(url);
            task.stop();
        }
    }

    /**
     * 根据Url取消一个下载任务
     * Cancel a download task base on url.
     *
     * @param url 文件下载地址
     *            Download url.
     */
    public void dlCancel(String url) {
        dlStop(url);
        if (null != sDBManager.queryTaskInfoByUrl(url)) {
            sDBManager.deleteTaskInfo(url);
            List<ThreadInfo> info = sDBManager.queryThreadInfos(url);
            if (null != info && info.size() != 0) {
                sDBManager.deleteThreadInfos(url);
            }
        }
    }

    /**
     * @see #dlStart(String, String, String, IDListener)
     */
    public void dlStart(String url) {
        dlStart(url, null);
    }

    /**
     * @see #dlStart(String, String, String, IDListener)
     */
    public void dlStart(String url, String dirPath) {
        dlStart(url, dirPath, null);
    }

    /**
     * @see #dlStart(String, String, String, IDListener)
     */
    public void dlStart(String url, String dirPath, IDListener listener) {
        dlStart(null, url, dirPath, listener);
    }

    /**
     * 开始一个下载任务
     * Start a download task.
     *
     * @param fileName 文件名，文件名需要包括文件扩展名，类似“AigeStudio.apk”的格式。该值可为空，为空时将由程
     *                 序决定文件名。为下载任务指定一个确切的文件名可大大提高程序的执行效率
     *                 Name of download file, include extension like "AigeStudio.apk". This
     *                 parameter can be null, in this case the file name will be decided by program.
     *                 You can specify a exact file name and it will increases the executive
     *                 efficiency.
     * @param url      文件下载地址
     *                 Download url.
     * @param dirPath  文件下载后保存的目录地址，该值为空时会默认使用应用的文件缓存目录作为保存目录地址
     *                 The directory of download file. This parameter can be null, in this case we
     *                 will use cache dir of app for download path.
     * @param listener 下载监听器
     *                 Listener of download task.
     */
    public void dlStart(String fileName, String url, String dirPath, IDListener listener) {
        if (TextUtils.isEmpty(url)) {
            if (DEBUG) Log.e(TAG, "Url can not be null");
            if (null != listener) listener.onError(ERROR_INVALID_URL, "Url can not be null");
            return;
        }
        if (!DLUtil.isNetworkAvailable(context)) {
            if (DEBUG) Log.e(TAG, "Network is not available.");
            if (null != listener) listener.onError(ERROR_NOT_NETWORK, "Network is not available.");
            return;
        }
        if (URL_DLING.contains(url)) {
            if (DEBUG) Log.e(TAG, url + " is downloading");
            if (null != listener) listener.onError(ERROR_REPEAT_URL, url + " is downloading.");
        } else {
            Info info = new Info(fileName, dirPath, url, listener);
            if (URL_DLING.size() >= maxTask) {
                if (DEBUG) Log.w(TAG, "Downloading urls is out of range.");
                TASK_PREPARE.add(info);
            } else {
                if (DEBUG) Log.d(TAG, "Prepare download from " + info.url);
                if (null != listener) listener.onPrepare();
                URL_DLING.add(info.url);
                DLPrepare dlPrepare = new DLPrepare(info);
                mPreExecutor.execute(dlPrepare);
            }
        }
    }

    private class DLPrepare implements Runnable {
        private String fileName, url, dirPath;
        private IDListener listener;
        private boolean isRedirect = true;

        DLPrepare(Info info) {
            fileName = info.fileName;
            url = info.url;
            dirPath = info.dirPath;
            listener = info.listener;
        }

        @Override
        public void run() {
            dlPrepare(url);
        }

        private void dlPrepare(String url) {
            HttpURLConnection conn = null;
            try {
                conn = DLUtil.buildConnection(url);
                if (TextUtils.isEmpty(fileName)) conn.setInstanceFollowRedirects(false);
                final int code = conn.getResponseCode();
                final String msg = conn.getResponseMessage();
                switch (code) {
                    case HTTP_OK:
                    case HTTP_PARTIAL:
                        isRedirect = false;
                        dlInit(url, conn);
                        break;
                    case HTTP_MOVED_PERM:
                    case HTTP_MOVED_TEMP:
                    case HTTP_SEE_OTHER:
                    case HTTP_NOT_MODIFIED:
                    case HTTP_TEMP_REDIRECT:
                        if (!TextUtils.isEmpty(fileName)) {
                            dlInit(url, conn);
                            break;
                        }
                        while (isRedirect) {
                            String realUrl = conn.getHeaderField(HttpConnPars.LOCATION.content);
                            if (TextUtils.isEmpty(realUrl)) {
                                if (DEBUG) Log.e(TAG, "Can not get the real url from redirect.");
                                if (null != listener)
                                    listener.onError(ERROR_CANNOT_GET_URL,
                                            "Can not get the real url from redirect.");
                                URL_DLING.remove(url);
                                isRedirect = false;
                                break;
                            }
                            if (DEBUG) Log.d(TAG, "Real url " + realUrl);
                            dlPrepare(realUrl);
                        }
                        break;
                    default:
                        if (DEBUG) Log.e(TAG, msg);
                        if (null != listener)
                            listener.onError(code, msg);
                        URL_DLING.remove(url);
                        isRedirect = false;
                        break;
                }
            } catch (IOException e) {
                if (DEBUG) Log.d(TAG, e.toString());
                if (null != listener)
                    listener.onError(ERROR_OPEN_CONNECT, e.toString());
                URL_DLING.remove(url);
                isRedirect = false;
            } finally {
                if (null != conn) conn.disconnect();
            }
        }

        private void dlInit(String url, HttpURLConnection conn) {
            if (TextUtils.isEmpty(fileName)) {
                final String disposition =
                        conn.getHeaderField(HttpConnPars.CONTENT_DISPOSITION.content);
                final String location = conn.getHeaderField(HttpConnPars.LOCATION.content);
                fileName = DLUtil.obtainFileName(url, disposition, location);
            }
            final int length = conn.getContentLength();
            if (DEBUG) Log.d(TAG, "File name " + fileName);
            if (DEBUG) Log.d(TAG, "File length " + length);
            if (null != listener) listener.onStart(fileName, url, length);
            TaskInfo info = sDBManager.queryTaskInfoByUrl(this.url);
            if (TextUtils.isEmpty(dirPath)) {
                dirPath = context.getCacheDir().getAbsolutePath();
            }
            File file = new File(dirPath, fileName);
            DLTask task;

            // 数据初始化操作
            // Data initialization.
            if (null == info) {
                if (!file.exists() || (file.exists() && file.length() != length && file.delete())) {
                    if (DEBUG) Log.d(TAG, this.url + " will be a new task with new file.");
                    if (!DLUtil.createFile(dirPath, fileName)) {
                        if (DEBUG) Log.d(TAG, this.url + " create file fail.");
                        if (null != listener)
                            listener.onError(ERROR_CREATE_FILE, this.url + " create file fail.");
                        return;
                    }
                    task = new DLTask(new TaskInfo(new File(dirPath, fileName), this.url, url, 0,
                            length), listener, false, null);
                } else {
                    if (DEBUG) Log.d(TAG, this.url + " was downloaded.");
                    if (null != listener) listener.onFinish(file);
                    URL_DLING.remove(this.url);
                    return;
                }
            } else {
                List<ThreadInfo> threadInfo = sDBManager.queryThreadInfos(this.url);
                if (!file.exists() || (file.exists() && threadInfo.size() == 0)) {

                    // 正常情况下不会执行到这里的逻辑判断
                    // This will not be execute in normal case.
                    if (DEBUG) Log.w(TAG, this.url +
                            " had a unhandled error and it will be restart for new task");
                    sDBManager.deleteTaskInfo(this.url);
                    sDBManager.deleteThreadInfos(this.url);
                    task = new DLTask(new TaskInfo(new File(dirPath, fileName), this.url, url, 0,
                            length), listener, false, null);
                } else {
                    if (DEBUG) Log.d(TAG, this.url + " will be resume.");
                    task = new DLTask(info, listener, true, threadInfo);
                }
            }
            mTaskExecutor.execute(task);
        }
    }

    private class DLTask implements Runnable, IDLThreadListener {
        private TaskInfo info;
        private IDListener listener;
        private ExecutorService mThreadExecutor;

        private int totalProgress, fileLength;
        private int totalProgressIn100;
        private boolean isStop, isResume;
        private List<ThreadInfo> mThreadInfo;

        DLTask(TaskInfo info, IDListener listener, boolean isResume,
               List<ThreadInfo> threadInfo) {
            this.info = info;
            this.listener = listener;
            this.isResume = isResume;
            mThreadInfo = threadInfo;
            totalProgress = info.progress;
            fileLength = info.length;
            mThreadExecutor = new ThreadPoolExecutor(CORES, CORES, 1, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>());
        }

        void stop() {
            if (DEBUG) Log.d(TAG, info.baseUrl + " will be stop.");
            isStop = true;
        }

        @Override
        public void run() {
            TASK_DLING.put(info.baseUrl, this);
            if (isResume) {
                if (DEBUG) Log.d(TAG, info.baseUrl + " resume from database.");
                for (ThreadInfo i : mThreadInfo) {
                    mThreadExecutor.execute(new DLThread(i, this));
                }
            } else {
                HttpURLConnection conn = null;
                try {
                    conn = DLUtil.buildConnection(info.realUrl);
                    conn.setRequestProperty("Range", "bytes=" + 0 + "-");
                    fileLength = conn.getContentLength();
                    final int code = conn.getResponseCode();
                    switch (code) {
                        case HTTP_OK:
                            if (DEBUG) Log.d(TAG, info.baseUrl + " will be download in normal.");
                            ThreadInfo tiNormal = new ThreadInfo(info.dlLocalFile, info.baseUrl,
                                    info.realUrl, 0, fileLength, UUID.randomUUID().toString());
                            mThreadExecutor.execute(new DLThread(tiNormal, this));
                            break;
                        case HTTP_PARTIAL:
                            if (DEBUG) Log.d(TAG, info.baseUrl + " will be download in partial.");
                            info.length = fileLength;
                            sDBManager.insertTaskInfo(info);

                            // 计算需要几个线程
                            // Calculate how many thread we will need.
                            int threadSize;
                            int threadLength = LENGTH_PER_THREAD;
                            if (fileLength <= LENGTH_PER_THREAD) {
                                threadSize = 3;
                                threadLength = fileLength / threadSize;
                            } else {
                                threadSize = fileLength / LENGTH_PER_THREAD;
                            }
                            if (DEBUG)
                                Log.d(TAG, info.baseUrl + " will be download with " +
                                        threadSize + " threads.");
                            int remainder = fileLength % threadLength;

                            // 计算每个线程下载多少长度并分发
                            // Calculate bytes will be downloaded for each thread.
                            for (int i = 0; i < threadSize; i++) {
                                int start = i * threadLength;
                                int end = start + threadLength - 1;
                                if (i == threadSize - 1) {
                                    end = start + threadLength + remainder;
                                }
                                String id = UUID.randomUUID().toString();
                                if (DEBUG)
                                    Log.d(TAG, info.baseUrl + " the thread" + i + " named " + id +
                                            " will download from " + start + " to " + end);
                                ThreadInfo tiPartial = new ThreadInfo(info.dlLocalFile,
                                        info.baseUrl, info.realUrl, start, end, id);
                                mThreadExecutor.execute(new DLThread(tiPartial, this));
                            }
                            break;
                        default:
                            final String msg = conn.getResponseMessage();
                            if (DEBUG) Log.e(TAG, msg);
                            if (null != listener) listener.onError(code, msg);
                            TASK_DLING.remove(info.baseUrl);
                            URL_DLING.remove(info.baseUrl);
                            break;
                    }
                } catch (IOException e) {
                    if (DEBUG) Log.d(TAG, e.toString());
                    if (null != listener)
                        listener.onError(ERROR_OPEN_CONNECT, e.toString());
                    if (null != sDBManager.queryTaskInfoByUrl(info.baseUrl)) {
                        info.progress = totalProgress;
                        sDBManager.updateTaskInfo(info);
                        if (DEBUG) Log.d(TAG, "Update download info with error.");
                        TASK_DLING.remove(info.baseUrl);
                        URL_DLING.remove(info.baseUrl);
                    }
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }

        @Override
        public synchronized void onThreadProgress(int progress) {
            totalProgress += progress;
            int tmp = (int) (totalProgress * 1.0 / fileLength * 100);
            if (null != listener && tmp != totalProgressIn100) {
                listener.onProgress(tmp);
                totalProgressIn100 = tmp;
            }
            if (fileLength == totalProgress) {
                if (DEBUG) Log.d(TAG, info.baseUrl + " download finish.");
                if (null != listener) listener.onFinish(info.dlLocalFile);
                sDBManager.deleteTaskInfo(info.baseUrl);
                TASK_DLING.remove(info.baseUrl);
                URL_DLING.remove(info.baseUrl);

                // 如果任务队列中有等待的任务则开启任务的下载
                // Start waiting task in TASK_PREPARE.
                if (!TASK_PREPARE.isEmpty())
                    mPreExecutor.execute(new DLPrepare(TASK_PREPARE.remove(0)));
            }
            if (isStop) {
                if (DEBUG)
                    Log.d(TAG, info.baseUrl + " is stop and it was download " +
                            totalProgress + " already.");
                if (null != listener)
                    listener.onStop(totalProgress);
                info.progress = totalProgress;
                sDBManager.updateTaskInfo(info);
                TASK_DLING.remove(info.baseUrl);
                URL_DLING.remove(info.baseUrl);
            }
        }

        private class DLThread implements Runnable {
            private ThreadInfo info;
            private IDLThreadListener listener;
            private int progress;

            DLThread(ThreadInfo info, IDLThreadListener listener) {
                this.info = info;
                this.listener = listener;
            }

            @Override
            public void run() {
                HttpURLConnection conn = null;
                RandomAccessFile raf = null;
                InputStream is = null;
                try {
                    conn = DLUtil.buildConnection(info.realUrl);
                    conn.setRequestProperty("Range", "bytes=" + info.start + "-" + info.end);
                    raf = new RandomAccessFile(info.dlLocalFile, DLCons.AccessModes.ACCESS_MODE_RWD);
                    final int code = conn.getResponseCode();
                    is = conn.getInputStream();
                    raf.seek(info.start);
                    byte[] b = new byte[4096];
                    int len;
                    switch (code) {
                        case HTTP_OK:
                            while (!isStop && (len = is.read(b)) != -1) {
                                raf.write(b, 0, len);
                                listener.onThreadProgress(len);
                            }
                            break;
                        case HTTP_PARTIAL:
                            if (!isResume) {
                                sDBManager.insertThreadInfo(info);
                            }
                            int total = info.end - info.start;
                            while (!isStop && (len = is.read(b)) != -1) {
                                raf.write(b, 0, len);
                                progress += len;
                                listener.onThreadProgress(len);
                                if (progress >= total) {
                                    sDBManager.deleteThreadInfoById(info.id);
                                }
                            }
                            if (isStop && null != sDBManager.queryThreadInfoById(info.id)) {
                                info.start = info.start + progress;
                                sDBManager.updateThreadInfo(info);
                            }
                            break;
                        case HTTP_REQUESTED_RANGE_NOT_SATISFIABLE:
                            if (DEBUG) Log.e(TAG, "Requested range not satisfiable.");
                            // Todo Requested range wrong. I had not idea how to detail this case yet, but in most case it will not happen.
                        default:
                            final String msg = conn.getResponseMessage();
                            if (DEBUG) Log.e(TAG, msg);
                            if (null != DLTask.this.listener)
                                DLTask.this.listener.onError(code, msg);
                            TASK_DLING.remove(info.baseUrl);
                            URL_DLING.remove(info.baseUrl);
                            break;
                    }
                } catch (IOException e) {
                    if (null != sDBManager.queryThreadInfoById(info.id)) {
                        info.start = info.start + progress;
                        sDBManager.updateThreadInfo(info);
                    }
                } finally {
                    try {
                        if (null != is) is.close();
                        if (null != raf) raf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (null != conn) conn.disconnect();
                }
            }
        }
    }

    private class Info {
        String fileName, dirPath, url;
        IDListener listener;

        public Info(String fileName, String dirPath, String url, IDListener listener) {
            this.fileName = fileName;
            this.dirPath = dirPath;
            this.url = url;
            this.listener = listener;
        }
    }

    private interface IDLThreadListener {
        void onThreadProgress(int progress);
    }
}