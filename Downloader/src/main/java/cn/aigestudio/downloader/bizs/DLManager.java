package cn.aigestudio.downloader.bizs;

import android.content.Context;
import android.widget.Toast;

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

import cn.aigestudio.downloader.cons.PublicCons;
import cn.aigestudio.downloader.entities.TaskInfo;
import cn.aigestudio.downloader.entities.ThreadInfo;
import cn.aigestudio.downloader.interfaces.DLTaskListener;
import cn.aigestudio.downloader.interfaces.IDLThreadListener;
import cn.aigestudio.downloader.utils.FileUtil;
import cn.aigestudio.downloader.utils.LogUtil;
import cn.aigestudio.downloader.utils.NetUtil;

/**
 * 下载管理器
 * 执行具体的下载操作
 * 开始一个下载任务只需调用{@link #dlStart}方法即可
 * 停止某个下载任务需要调用{@link #dlStop}方法 停止下载任务仅仅会将对应下载任务移除下载队列而不删除相应数据 下次启动相同任务时会自动根据上一次停止时保存的数据重新开始下载
 * 取消某个下载任务需要调用{@link #dlCancel}方法 取消下载任务会删除掉相应的本地数据库数据但文件不会被删除
 * 相同url的下载任务视为相同任务
 * Download manager
 * Use {@link #dlStart} for a new download task.
 * Use {@link #dlStop} to stop a download task base on url.
 * Use {@link #dlCancel} to cancel a download task base on url.
 * By the way, the difference between {@link #dlStop} and {@link #dlCancel} is whether the data in database would be deleted or not,
 * for example, the state of download like local file and data in database will be save when you use {@link #dlStop} stop a download task,
 * if you use {@link #dlCancel} cancel a download task, anything related to download task would be deleted.
 *
 * @author AigeStudio 2015-05-09
 */
public final class DLManager {
    private static final int THREAD_POOL_SIZE = 32;

    private static DLManager sManager;
    private static Hashtable<String, DLTask> sTaskDLing;
    private static DBManager sDBManager;

    private ExecutorService mExecutor;
    private Context context;

    public DLManager(Context context) {
        this.context = context;
        this.mExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        sDBManager = DBManager.getInstance(context);
        sTaskDLing = new Hashtable<>();
    }

    /**
     * 获取下载管理器单例对象
     * Singleton of DLManager.
     *
     * @param context ...
     * @return 下载管理器单例对象
     */
    public static DLManager getInstance(Context context) {
        if (null == sManager) {
            sManager = new DLManager(context);
        }
        return sManager;
    }

    /**
     * 开始一个下载任务
     * Start a download task.
     *
     * @param url      下载地址 url of remote file
     * @param dirPath  下载文件保存目录 local directory for file save
     * @param listener 下载监听对象 listener of download
     */
    public void dlStart(String url, String dirPath, DLTaskListener listener) {
        if (sTaskDLing.containsKey(url)) {
            Toast.makeText(context, "文件正在下载", Toast.LENGTH_SHORT).show();
            return;
        }
        TaskInfo info = sDBManager.queryTaskInfoByUrl(url);

        String fileName = FileUtil.getFileNameFromUrl(url);
        File file = new File(dirPath, fileName);

        if (null == info || !file.exists()) {
            LogUtil.i("New Task!");
            info = new TaskInfo(FileUtil.createFile(dirPath, fileName), url, 0, 0);
        }
        DLTask task = new DLTask(info, listener);

        mExecutor.execute(task);
    }

    /**
     * 根据下载地址停止下载任务
     * Stop a download task base on url
     *
     * @param url 下载地址 url of remote file
     */
    public void dlStop(String url) {
        if (sTaskDLing.containsKey(url)) {
            DLTask task = sTaskDLing.get(url);
            task.setStop(true);
        }
    }

    /**
     * 根据下载地址停取消下载任务
     * Cancel a download task base on url
     *
     * @param url 下载地址 url of remote file
     */
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
     * 释放资源 暂未使用
     * Release resource. No use.
     */
    public void release() {
        mExecutor.shutdown();
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
            LogUtil.i("Last time we were download " + totalProgress + "byte.");
            this.fileLength = info.length;

            if (null != sDBManager.queryTaskInfoByUrl(info.url)) {
                if (!info.dlLocalFile.exists()) {
                    sDBManager.deleteTaskInfo(info.url);
                }
                mThreadInfos = sDBManager.queryThreadInfos(info.url);
                if (null != mThreadInfos && mThreadInfos.size() != 0) {
                    isResume = true;
                } else {
                    sDBManager.deleteTaskInfo(info.url);
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
                sTaskDLing.put(info.url, this);

                if (isResume) {
                    LogUtil.i("Resume download.");
                    for (ThreadInfo i : mThreadInfos) {
                        mExecutor.execute(new DLThread(i, this));
                    }
                } else {
                    HttpURLConnection conn = null;
                    try {
                        conn = NetUtil.buildConnection(info.url);

                        if (conn.getResponseCode() == HttpStatus.SC_OK) {
                            fileLength = conn.getContentLength();

                            if (info.dlLocalFile.exists() && info.dlLocalFile.length() == fileLength) {
                                LogUtil.i("File exists!");
                                isExists = true;
                                sTaskDLing.remove(info.url);
                                if (null != mListener) mListener.onFinish(info.dlLocalFile);
                            }
                            if (!isExists) {
                                info.length = fileLength;
                                sDBManager.insertTaskInfo(info);

                                int threadSize = fileLength / LENGTH_PER_THREAD;
                                LogUtil.i("We will start " + threadSize + " threads.");
                                int remainder = fileLength % LENGTH_PER_THREAD;
                                LogUtil.i("The last thread will download " + remainder + " more bytes.");
                                for (int i = 0; i < threadSize; i++) {
                                    int start = i * LENGTH_PER_THREAD;
                                    int end = start + LENGTH_PER_THREAD - 1;
                                    if (i == threadSize - 1) {
                                        end = start + LENGTH_PER_THREAD + remainder;
                                    }
                                    String id = UUID.randomUUID().toString();
                                    LogUtil.i("The thread " + id + " will download from " + start + " to " +
                                            "" + end + " byte.");
                                    ThreadInfo ti = new ThreadInfo(info.dlLocalFile, info.url, start,
                                            end, id);

                                    mExecutor.execute(new DLThread(ti, this));
                                }
                            }
                        }
                    } catch (Exception e) {
                        if (null != sDBManager.queryTaskInfoByUrl(info.url)) {
                            info.progress = totalProgress;
                            sDBManager.updateTaskInfo(info);
                            sTaskDLing.remove(info.url);
                        }
                        if (null != mListener) mListener.onError(e.getMessage());
                    } finally {
                        if (conn != null) {
                            conn.disconnect();
                        }
                    }
                }
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
                    LogUtil.i(info.url + " download finish!");
                    sDBManager.deleteTaskInfo(info.url);
                    sTaskDLing.remove(info.url);
                    if (null != mListener) mListener.onFinish(info.dlLocalFile);
                }
                if (isStop) {
                    LogUtil.i("We were downloaded " + totalProgress + " bytes already.");
                    info.progress = totalProgress;
                    sDBManager.updateTaskInfo(info);
                    sTaskDLing.remove(info.url);
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
                LogUtil.i("Thread " + info.id + " will start from " + info.start + " to " + info.end + " bytes.");
            }

            @Override
            public void run() {
                HttpURLConnection conn = null;
                RandomAccessFile raf = null;
                InputStream is = null;
                try {
                    conn = NetUtil.buildConnection(info.url);
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
                                LogUtil.i("Thread " + info.id + " finish!");
                                sDBManager.deleteThreadInfoById(info.id);
                            }
                        }
                        if (isStop && null != sDBManager.queryThreadInfoById(info.id)) {
                            info.start = info.start + progress;
                            sDBManager.updateThreadInfo(info);
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
