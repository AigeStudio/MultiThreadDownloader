package cn.aigestudio.downloader.bizs;

import android.content.Context;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.aigestudio.downloader.cons.HttpConnPars;
import cn.aigestudio.downloader.cons.PublicCons;
import cn.aigestudio.downloader.entities.TaskInfo;
import cn.aigestudio.downloader.entities.ThreadInfo;
import cn.aigestudio.downloader.interfaces.DLTaskListener;
import cn.aigestudio.downloader.interfaces.IDLThreadListener;
import cn.aigestudio.downloader.utils.FileUtil;
import cn.aigestudio.downloader.utils.NetUtil;

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
 */
public final class DLManager {
    private static final int THREAD_POOL_SIZE = 32;

    private static DLManager sManager;
    private static DBManager sDBManager;
    /**
     * 任务列表
     */
    private static Hashtable<String, DLTask> sTaskDLing;


    private ExecutorService mExecutor;
    private Context context;

    public DLManager(Context context) {
        this.context = context;
        this.mExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        sDBManager = DBManager.getInstance(context);
        sTaskDLing = new Hashtable<>();
    }

    public static DLManager getInstance(Context context) {
        if (null == sManager) {
            sManager = new DLManager(context);
        }
        return sManager;
    }

    public void dlStart(String url, String dirPath, DLTaskListener listener) {
        DLPrepare dlPrepare = new DLPrepare(url, dirPath, listener);
        mExecutor.execute(dlPrepare);
    }

    public void dlStop(String url) {
        if (sTaskDLing.containsKey(url)) {
            DLTask task = sTaskDLing.get(url);
            task.setStop(true);
        }
    }

    public void dlCancel(String url) {
        dlStop(url);
        if (null != sDBManager.queryTaskInfoByUrl(url)) {
            sDBManager.deleteTaskInfo(url);
            List<ThreadInfo> infos = sDBManager.queryThreadInfos(url);
            if (null != infos && infos.size() != 0) {
                sDBManager.deleteThreadInfos(url);
            }
        }
    }

    /**
     * 文件已经开始下载错误提示
     */
    public static final String ERROR_DOWNLOADING = "File is downloading";
    /**
     * 下载失败：没有网络 错误提示
     */
    public static final String ERROR_NO_NETWORK = "no_network";

    private class DLPrepare implements Runnable {
        private String url, dirPath;// 下载路径和保存目录
        private DLTaskListener listener;// 下载监听器

        private DLPrepare(String url, String dirPath, DLTaskListener listener) {
            this.url = url;
            this.dirPath = dirPath;
            this.listener = listener;
        }

        @Override
        public void run() {
            HttpURLConnection conn = null;
            try {
                String realUrl = url;
                conn = NetUtil.buildConnection(url);
                conn.setInstanceFollowRedirects(false);
                conn.setRequestProperty(HttpConnPars.REFERER.content, url);
                if (conn.getResponseCode() == HttpStatus.SC_MOVED_TEMPORARILY ||
                        conn.getResponseCode() == HttpStatus.SC_MOVED_PERMANENTLY) {
                    realUrl = conn.getHeaderField(HttpConnPars.LOCATION.content);
                }
                synchronized (sTaskDLing){//fix: 如果文件正在取消或异常，这里不能立即重新开始，表现为当多次点击下载时：1. 同时引发多个任务下载；2. 点击无效且无任何返回值；需要进行并发线程的业务处理；
                    // 如果文件正在下载
//                    DLTask dlTask = sTaskDLing.get(url);
//                    if (dlTask!=null&&(!dlTask.isStop)) {
                    if (sTaskDLing.contains(url)) {
                        // 文件正在下载 File is downloading
                        if(listener!=null)listener.onError(ERROR_DOWNLOADING);
                    } else {
                        TaskInfo info = sDBManager.queryTaskInfoByUrl(url);
                        String fileName = FileUtil.getFileNameFromUrl(realUrl).replace("/", "");
                        if (null != listener) listener.onStart(fileName, realUrl);
                        File file = new File(dirPath, fileName);
                        if (null == info || !file.exists()) {
                            info = new TaskInfo(FileUtil.createFile(dirPath, fileName), url, realUrl, 0, 0);
                        }
                        DLTask task = new DLTask(info, listener);
                        sTaskDLing.put(info.baseUrl, task);
                        mExecutor.execute(task);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != conn) {
                    conn.disconnect();
                }
            }
        }
    }

    private class DLTask implements Runnable, IDLThreadListener {
        private static final int LENGTH_PER_THREAD = 2097152;

        private TaskInfo info;
        private DLTaskListener mListener;

        private int totalProgress, fileLength;
        private int totalProgressIn100;
        private boolean isResume;
        private boolean isStop;
        private boolean isExists;
        private boolean isConnect = true;

        private List<ThreadInfo> mThreadInfos;

        private DLTask(TaskInfo info, DLTaskListener listener) {
            this.info = info;
            this.mListener = listener;
            this.totalProgress = info.progress;
            this.fileLength = info.length;

            if (null != sDBManager.queryTaskInfoByUrl(info.baseUrl)) {
                if (!info.dlLocalFile.exists()) {
                    sDBManager.deleteTaskInfo(info.baseUrl);
                }
                mThreadInfos = sDBManager.queryThreadInfos(info.baseUrl);
                if (null != mThreadInfos && mThreadInfos.size() != 0) {
                    isResume = true;
                } else {
                    sDBManager.deleteTaskInfo(info.baseUrl);
                }
            }
        }

        public void setStop(boolean isStop) {
            this.isStop = isStop;
        }

        @Override
        public void run() {
            if (NetUtil.getNetWorkType(context) == PublicCons.NetType.INVALID) {
                if (null != mListener)
                    mListener.onConnect(PublicCons.NetType.INVALID, "无网络连接");
                isConnect = false;
            } else if (NetUtil.getNetWorkType(context) == PublicCons.NetType.NO_WIFI) {
                if (null != mListener)
                    isConnect = mListener.onConnect(PublicCons.NetType.NO_WIFI, "正在使用非WIFI网络下载");
            }
            if (isConnect) {
                if (isResume) {
                    for (ThreadInfo i : mThreadInfos) {
                        mExecutor.execute(new DLThread(i, this));
                    }
                } else {
                    HttpURLConnection conn = null;
                    try {
                        conn = NetUtil.buildConnection(info.realUrl);
                        conn.setRequestProperty("Range", "bytes=" + 0 + "-" + Integer.MAX_VALUE);
                        if (conn.getResponseCode() == HttpStatus.SC_PARTIAL_CONTENT) {
                            fileLength = conn.getContentLength();
                            if (info.dlLocalFile.exists() && info.dlLocalFile.length() == fileLength) {
                                isExists = true;
                                sTaskDLing.remove(info.baseUrl);
                                if (null != mListener) mListener.onFinish(info.dlLocalFile);
                            }
                            if (!isExists) {
                                info.length = fileLength;
                                sDBManager.insertTaskInfo(info);
                                int threadSize;
                                int length = LENGTH_PER_THREAD;
                                if (fileLength <= LENGTH_PER_THREAD) {
                                    threadSize = 3;
                                    length = fileLength / threadSize;
                                } else {
                                    threadSize = fileLength / LENGTH_PER_THREAD;
                                }
                                int remainder = fileLength % length;
                                for (int i = 0; i < threadSize; i++) {
                                    int start = i * length;
                                    int end = start + length - 1;
                                    if (i == threadSize - 1) {
                                        end = start + length + remainder;
                                    }
                                    String id = UUID.randomUUID().toString();
                                    ThreadInfo ti = new ThreadInfo(info.dlLocalFile,
                                            info.baseUrl, info.realUrl, start, end, id);

                                    mExecutor.execute(new DLThread(ti, this));
                                }
                            }
                        } else if (conn.getResponseCode() == HttpStatus.SC_OK) {
                            fileLength = conn.getContentLength();
                            if (info.dlLocalFile.exists() && info.dlLocalFile.length() == fileLength) {
                                sTaskDLing.remove(info.baseUrl);
                                if (null != mListener) mListener.onFinish(info.dlLocalFile);
                            } else {
                                ThreadInfo ti = new ThreadInfo(info.dlLocalFile, info.baseUrl,
                                        info.realUrl, 0, fileLength, UUID.randomUUID().toString());
                                mExecutor.execute(new DLThread(ti, this));
                            }
                        }
                    } catch (Exception e) {
                        if (null != sDBManager.queryTaskInfoByUrl(info.baseUrl)) {
                            info.progress = totalProgress;
                            sDBManager.updateTaskInfo(info);
                            sTaskDLing.remove(info.baseUrl);
                        }
                        if (null != mListener) mListener.onError(e.getMessage());
                    } finally {
                        if (conn != null) {
                            conn.disconnect();
                        }
                    }
                }
            }else{
                //下载失败：网络异常
                sTaskDLing.remove(info.baseUrl);
                if (null != mListener) mListener.onError(ERROR_NO_NETWORK);
            }
        }

        @Override
        public void onThreadProgress(int progress) {
            synchronized (this) {
                totalProgress += progress;
                int tmp = (int) (totalProgress * 1.0 / fileLength * 100);
                if (null != mListener && tmp != totalProgressIn100) {
                    mListener.onProgress(tmp);
                    totalProgressIn100 = tmp;
                }
                if (fileLength == totalProgress) {
                    sDBManager.deleteTaskInfo(info.baseUrl);
                    sTaskDLing.remove(info.baseUrl);
                    if (null != mListener) mListener.onFinish(info.dlLocalFile);
                }
                if (isStop) {
                    info.progress = totalProgress;
                    sDBManager.updateTaskInfo(info);
                    sTaskDLing.remove(info.baseUrl);
                }
            }
        }

        private class DLThread implements Runnable {
            private ThreadInfo info;
            private IDLThreadListener mListener;

            private int progress;

            public DLThread(ThreadInfo info, IDLThreadListener listener) {
                this.info = info;
                this.mListener = listener;
            }

            @Override
            public void run() {
                HttpURLConnection conn = null;
                RandomAccessFile raf = null;
                InputStream is = null;
                try {
                    conn = NetUtil.buildConnection(info.realUrl);
                    conn.setRequestProperty("Range", "bytes=" + info.start + "-" + info.end);

                    raf = new RandomAccessFile(info.dlLocalFile,
                            PublicCons.AccessModes.ACCESS_MODE_RWD);
                    if (conn.getResponseCode() == HttpStatus.SC_PARTIAL_CONTENT) {
                        if (!isResume) {
                            sDBManager.insertThreadInfo(info);
                        }
                        is = conn.getInputStream();
                        raf.seek(info.start);
                        int total = info.end - info.start;
                        byte[] b = new byte[1024];
                        int len;
                        while (!isStop && (len = is.read(b)) != -1) {
                            raf.write(b, 0, len);
                            progress += len;
                            mListener.onThreadProgress(len);
                            if (progress >= total) {
                                sDBManager.deleteThreadInfoById(info.id);
                            }
                        }
                        if (isStop && null != sDBManager.queryThreadInfoById(info.id)) {
                            info.start = info.start + progress;
                            sDBManager.updateThreadInfo(info);
                        }
                    } else if (conn.getResponseCode() == HttpStatus.SC_OK) {
                        is = conn.getInputStream();
                        raf.seek(info.start);
                        byte[] b = new byte[1024];
                        int len;
                        while (!isStop && (len = is.read(b)) != -1) {
                            raf.write(b, 0, len);
                            mListener.onThreadProgress(len);
                        }
                    }
                } catch (Exception e) {
                    if (null != sDBManager.queryThreadInfoById(info.id)) {
                        info.start = info.start + progress;
                        sDBManager.updateThreadInfo(info);
                    }
                } finally {
                    try {
                        if (null != is) {
                            is.close();
                        }
                        if (null != raf) {
                            raf.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (null != conn) {
                        conn.disconnect();
                    }
                }
            }
        }
    }
}
