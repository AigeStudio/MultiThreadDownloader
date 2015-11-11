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

//import static cn.aigestudio.downloader.bizs.DLCons.DEBUG;
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

///**
// * 下载管理器
// * Download manager
// * 执行具体的下载操作
// *
// * @author AigeStudio 2015-05-09
// *         开始一个下载任务只需调用{@link #dlStart}方法即可
// *         停止某个下载任务需要调用{@link #dlStop}方法 停止下载任务仅仅会将对应下载任务移除下载队列而不删除相应数据 下次启动相同任务时会自动根据上一次停止时保存的数据重新开始下载
// *         取消某个下载任务需要调用{@link #dlCancel}方法 取消下载任务会删除掉相应的本地数据库数据但文件不会被删除
// *         相同url的下载任务视为相同任务
// *         Use {@link #dlStart} for a new download task.
// *         Use {@link #dlStop} to stop a download task base on url.
// *         Use {@link #dlCancel} to cancel a download task base on url.
// *         By the way, the difference between {@link #dlStop} and {@link #dlCancel} is whether the data in database would be deleted or not,
// *         for example, the state of download like local file and data in database will be save when you use {@link #dlStop} stop a download task,
// *         if you use {@link #dlCancel} cancel a download task, anything related to download task would be deleted.
// * @author AigeStudio 2015-05-26
// *         对不支持断点下载的文件直接使用单线程下载 该操作将不会插入数据库
// *         对转向地址进行解析
// *         更改下载线程分配逻辑
// *         DLManager will download with single thread if server does not support break-point, and it will not insert to database
// *         Support url redirection.
// *         Change download thread size dispath.
// * @author AigeStudio 2015-05-29
// *         修改域名重定向后无法多线程下载问题
// *         修改域名重定向后无法暂停问题
// *         Bugfix:can not start multi-threads to download file when we in url redirection.
// *         Bugfix:can not stop a download task when we in url redirection.
// * @author zhangchi 2015-10-13
// *         Bugfix：修改多次触发任务时的并发问题，防止同时触发多个相同的下载任务；修改任务队列为线程安全模式；
// *         修改多线程任务的线程数量设置机制，每个任务可以自定义设置下载线程数量；通过同构方法dlStart(String url, String dirPath, DLTaskListener listener,int threadNum)；
// *         添加日志开关及日志记录，开关方法为setDebugEnable，日志TAG为DLManager；方便调试;
// * @author AigeStudio 2015-10-23
// *         修复大部分已知Bug
// *         优化代码逻辑适应更多不同的下载情况
// *         完善错误码机制，使用不同的错误码标识不同错误的发生，详情请参见{@link DLError}
// *         不再判断网络类型只会对是否联网做一个简单的判断
// *         修改{@link #setDebugEnable(boolean)}方法
// *         新增多个不同的{@link #dlStart}方法便于回调
// *         新增{@link #setMaxTask(int)}方法限制多个下载任务的并发数
// */
public final class DLManager {
    private static final int CORES = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService POOL_TASK = Executors.newCachedThreadPool();
    private static final ExecutorService POOL_Thread = Executors.newCachedThreadPool();
    private static DLManager sManager;

    private Context context;

    private DLManager(Context context) {
        this.context = context;
    }

    public static DLManager getInstance(Context context) {
        if (null == sManager) {
            sManager = new DLManager(context);
        }
        return sManager;
    }

    public void dlStart(String url, String dir, String name, List<DLHeader> headers, IDListener listener) {
        // 下载地址为空直接报错
        if (TextUtils.isEmpty(url)) {
            throw new RuntimeException("Url can not be null!");
        }
        DLInfo info = new DLInfo();
        info.baseUrl = url;
        info.realUrl = url;

        // 保存目录路径为空获取应用缓存目录路径
        if (TextUtils.isEmpty(dir)) {
            dir = context.getCacheDir().getAbsolutePath();
        }
        info.dirPath = dir;
        info.fileName = name;
        info.requestHeaders = DLUtil.initRequestHeaders(headers, info);
        info.listener = listener;
        info.hasListener = info.listener != null;
        POOL_TASK.execute(new DLTask(context, info));
    }

    synchronized static void addDLThread(DLThread thread) {
        POOL_Thread.execute(thread);
    }
}