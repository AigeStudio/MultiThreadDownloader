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
 * 取消某个下载任务需要调用{@link #dlCancle}方法 取消下载任务会删除掉相应的本地数据库数据但文件不会被删除
 * 相同url的下载任务视为相同任务
 *
 * @author AigeStudio 2015-05-09
 */
public final class DLManager {
    private static final int THREAD_POOL_SIZE = 8;// 线程池大小

    private static DLManager sManager;// 下载管理器的静态引用
    private static Hashtable<String, DLTask> sTaskDLing; // 静态Hashtable用于存储正在下载的任务
    private static DBManager sDBManager;// 数据库管理器的静态引用

    private ExecutorService mExecutor;// 线程池业务引用
    private Context context;// ...

    public DLManager(Context context) {
        // 初始化对象
        this.context = context;
        this.mExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        sDBManager = DBManager.getInstance(context);
        sTaskDLing = new Hashtable<>();
    }

    /**
     * 获取下载管理器单例对象
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
     *
     * @param url      下载地址
     * @param dirPath  下载文件保存目录
     * @param listener 下载监听对象
     */
    public void dlStart(String url, String dirPath, DLTaskListener listener) {
        // 如果传入的url已存在于Map中则表示该文件正在下载
        if (sTaskDLing.containsKey(url)) {
            Toast.makeText(context, "文件正在下载", Toast.LENGTH_SHORT).show();
            return;
        }
        // 尝试根据该url从数据库获取数据信息
        TaskInfo info = sDBManager.queryTaskInfoByUrl(url);

        // 根据url和文件保存目录路径生成文件File对象
        String fileName = FileUtil.getFileNameFromUrl(url);
        File file = new File(dirPath, fileName);

        // 数据库没有对应信息或者对应文件不存在则表示新建下载任务
        if (null == info || !file.exists()) {
            LogUtil.i("New Task!");
            info = new TaskInfo(FileUtil.createFile(dirPath, fileName), url, 0, 0);
        }

        // 构造下载任务对象
        DLTask task = new DLTask(info, listener);

        // 提交并执行任务
        mExecutor.execute(task);
    }

    /**
     * 根据下载地址停止下载任务
     *
     * @param url 下载地址
     */
    public void dlStop(String url) {
        // 如果在下载在队列中包含该url则根据该url取出相应的下载任务对象并设置停止下载
        if (sTaskDLing.containsKey(url)) {
            DLTask task = sTaskDLing.get(url);
            task.setStop(true);
        }
    }

    /**
     * 根据下载地址停取消下载任务
     *
     * @param url 下载地址
     */
    public void dlCancle(String url) {
        // 取消前先停止
        dlStop(url);

        // 根据url从数据库查找相应数据并删除
        if (null != sDBManager.queryTaskInfoByUrl(url)) {
            sDBManager.deleteTaskInfo(url);
            List<ThreadInfo> infos = sDBManager.queryThreadInfos(url);
            if (null != infos && infos.size() != 0) {
                sDBManager.deleteThreadInfos(url);
            }
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        mExecutor.shutdown();
    }

    /**
     * 具体的下载任务Runnable类
     * 该类主要任务为读取服务器文件大小并生成具体的下载线程分发下载
     *
     * @author AigeStudio 2015-05-09
     */
    private class DLTask implements Runnable, IDLThreadListener {
        private static final int LENGTH_PER_THREAD = 5242880;// 单个线程下载的最大长度

        private TaskInfo info;// 下载任务实体对象
        private DLTaskListener mListener;// 下载任务监听器

        private int totalProgress, fileLength;// 下载总进度和文件长度
        private int totalProgressIn100;// 以100为最大值的进度表示
        private boolean isResume;// 标识是否断点续传
        private boolean isStop;// 标识下载任务是否暂停
        private boolean isExists;// 标识文件是否存在
        private boolean isConnect = true;// 标识网络是否连接 默认恒为连接

        private List<ThreadInfo> mThreadInfos;// 如果为断点续传则该引用不为空

        private DLTask(TaskInfo info, DLTaskListener listener) {
            // 初始化对象
            this.info = info;
            this.mListener = listener;
            this.totalProgress = info.progress;
            LogUtil.i("Last time we were download " + totalProgress + "byte.");
            this.fileLength = info.length;

            // 查询数据库信息判断是否存在该任务信息
            if (null != sDBManager.queryTaskInfoByUrl(info.url)) {
                // 如果数据库有信息但文件不存在则删除数据库信息
                if (!info.dlLocalFile.exists()) {
                    sDBManager.deleteTaskInfo(info.url);
                }
                // 如果存在该信息那么继续判断是否拥有对应的线程
                mThreadInfos = sDBManager.queryThreadInfos(info.url);
                if (null != mThreadInfos && mThreadInfos.size() != 0) {
                    // 如果有对应的下载线程则表示可以断点续传
                    isResume = true;
                } else {
                    // 否则视为异常情况删除该下载数据信息重建
                    sDBManager.deleteTaskInfo(info.url);
                }
            }
        }

        /**
         * 对外接口停止下载任务
         *
         * @param isStop ...
         */
        public void setStop(boolean isStop) {
            this.isStop = isStop;
        }

        @Override
        public void run() {
            // 判断网络连接状态
            if (NetUtil.getNetWorkType(context) == PublicCons.NetType.INVALID) {
                if (null != mListener)
                    mListener.onConnect(PublicCons.NetType.INVALID, "无网络连接");
                isConnect = false;
            } else if (NetUtil.getNetWorkType(context) == PublicCons.NetType.NO_WIFI) {
                if (null != mListener)
                    isConnect = mListener.onConnect(PublicCons.NetType.NO_WIFI, "正在使用非WIFI网络下载");
            }
            // 如果网络可用
            if (isConnect) {
                // 将该任务添加至Map
                sTaskDLing.put(info.url, this);

                // 如果为断点续传则获取存储于数据库的下载线程实体对象构造下载线程执行
                if (isResume) {
                    LogUtil.i("Resume download.");
                    for (ThreadInfo i : mThreadInfos) {
                        mExecutor.execute(new DLThread(i, this));
                    }
                } else {
                    // 声明HttpURLConnection引用
                    HttpURLConnection conn = null;
                    try {
                        // 打开连接
                        conn = NetUtil.buildConnection(info.url);

                        // 如果网路连通
                        if (conn.getResponseCode() == HttpStatus.SC_OK) {
                            // 获取文件长度大小
                            fileLength = conn.getContentLength();

                            // 如果文件已存在并长度一致则直接返回
                            if (info.dlLocalFile.exists() && info.dlLocalFile.length() == fileLength) {
                                LogUtil.i("File exists!");
                                isExists = true;
                                sTaskDLing.remove(info.url);
                                if (null != mListener) mListener.onFinish(info.dlLocalFile);
                            }

                            // 如果文件不存在则下载
                            if (!isExists) {
                                info.length = fileLength;

                                // 将下载任务插入数据库
                                sDBManager.insertTaskInfo(info);

                                // 计算每个线程需要下载多少
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
                                    // 构造下载线程对象
                                    ThreadInfo ti = new ThreadInfo(info.dlLocalFile, info.url, start,
                                            end, id);

                                    // 构造下载线程并执行
                                    mExecutor.execute(new DLThread(ti, this));
                                }
                            }
                        }
                    } catch (Exception e) {
                        // 出现异常保存数据
                        if (null != sDBManager.queryTaskInfoByUrl(info.url)) {
                            info.progress = totalProgress;
                            sDBManager.updateTaskInfo(info);
                            sTaskDLing.remove(info.url);
                        }
                        if (null != mListener) mListener.onError(e.getMessage());
                    } finally {
                        // 释放资源
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
                // 下载完成删除数据
                if (fileLength == totalProgress) {
                    LogUtil.i(info.url + " download finish!");
                    sDBManager.deleteTaskInfo(info.url);
                    sTaskDLing.remove(info.url);
                    if (null != mListener) mListener.onFinish(info.dlLocalFile);
                }
                // 停止下载存储数据
                if (isStop) {
                    LogUtil.i("We were downloaded " + totalProgress + " bytes already.");
                    info.progress = totalProgress;
                    sDBManager.updateTaskInfo(info);
                    sTaskDLing.remove(info.url);
                }
            }
        }

        /**
         * 真正执行下载的Runnable类
         */
        private class DLThread implements Runnable {
            private ThreadInfo info;// 线程实体对象
            private IDLThreadListener mListener;// 当前线程的下载监听器

            private int progress;// 存储下载进度

            public DLThread(ThreadInfo info, IDLThreadListener listener) {
                this.info = info;
                this.mListener = listener;
                LogUtil.i("Thread " + info.id + " will start from " + info.start + " to " + info.end + " bytes.");
            }

            @Override
            public void run() {
                // 声明资源引用
                HttpURLConnection conn = null;
                RandomAccessFile raf = null;
                InputStream is = null;
                try {
                    // 打开连接设置请求头
                    conn = NetUtil.buildConnection(info.url);
                    conn.setRequestProperty("Range", "bytes=" + info.start + "-" + info.end);

                    // 构造RandomAccessFile对象准备读写文件
                    raf = new RandomAccessFile(info.dlLocalFile,
                            PublicCons.AccessModes.ACCESS_MODE_RWD);

                    // 如果服务器支持分段读取
                    if (conn.getResponseCode() == HttpStatus.SC_PARTIAL_CONTENT) {
                        // 网络连通且不为续传时开始写入数据库
                        if (!isResume) {
                            sDBManager.insertThreadInfo(info);
                        }

                        // 获取输入流
                        is = conn.getInputStream();

                        // 跳过一定字节数设置字节写入的开始位置
                        raf.seek(info.start);

                        // 计算当前线程应该下载的字节大小
                        int total = info.end - info.start;

                        // 读取&下载文件字节
                        byte[] b = new byte[1024];
                        int len;
                        while (!isStop && (len = is.read(b)) != -1) {
                            raf.write(b, 0, len);

                            // 累加当前进程下载进度
                            progress += len;

                            // 回调注册的监听器返回该线程该次下载长度
                            mListener.onThreadProgress(len);

                            // 如果progress == total则表示当前线程已下载完成
                            if (progress >= total) {
                                LogUtil.i("Thread " + info.id + " finish!");
                                // 此时可以将其从数据库删除
                                sDBManager.deleteThreadInfoById(info.id);
                            }
                        }
                        // 停止后存储数据
                        if (isStop && null != sDBManager.queryThreadInfoById(info.id)) {
                            info.start = info.start + progress;
                            sDBManager.updateThreadInfo(info);
                        }
                    }
                } catch (Exception e) {
                    // 出现异常保存数据
                    if (null != sDBManager.queryThreadInfoById(info.id)) {
                        info.start = info.start + progress;
                        sDBManager.updateThreadInfo(info);
                    }
                } finally {
                    // 释放资源
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
