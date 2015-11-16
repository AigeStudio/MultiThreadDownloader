package cn.aigestudio.downloader.bizs;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import cn.aigestudio.downloader.interfaces.IDListener;

import static cn.aigestudio.downloader.bizs.DLCons.DEBUG;
import static cn.aigestudio.downloader.bizs.DLError.ERROR_INVALID_URL;
import static cn.aigestudio.downloader.bizs.DLError.ERROR_NOT_NETWORK;
import static cn.aigestudio.downloader.bizs.DLError.ERROR_REPEAT_URL;

public final class DLManager {
    private static final String TAG = DLManager.class.getSimpleName();

    private static final int CORES = Runtime.getRuntime().availableProcessors();
    private static final int POOL_SIZE = CORES + 1;
    private static final int POOL_SIZE_MAX = CORES * 2 + 1;

    private static final BlockingQueue<Runnable> POOL_QUEUE_TASK = new LinkedBlockingQueue<>(56);
    private static final BlockingQueue<Runnable> POOL_QUEUE_THREAD = new LinkedBlockingQueue<>(256);

    private static final ThreadFactory TASK_FACTORY = new ThreadFactory() {
        private final AtomicInteger COUNT = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "DLTask #" + COUNT.getAndIncrement());
        }
    };

    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        private final AtomicInteger COUNT = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "DLThread #" + COUNT.getAndIncrement());
        }
    };

    private static final ExecutorService POOL_TASK = new ThreadPoolExecutor(POOL_SIZE,
            POOL_SIZE_MAX, 3, TimeUnit.SECONDS, POOL_QUEUE_TASK, TASK_FACTORY);
    private static final ExecutorService POOL_Thread = new ThreadPoolExecutor(POOL_SIZE * 5,
            POOL_SIZE_MAX * 5, 1, TimeUnit.SECONDS, POOL_QUEUE_THREAD, THREAD_FACTORY);

    private static final ConcurrentHashMap<String, DLInfo> TASK_DLING = new ConcurrentHashMap<>();
    private static final List<DLInfo> TASK_PREPARE = Collections.synchronizedList(new ArrayList<DLInfo>());
    private static final ConcurrentHashMap<String, DLInfo> TASK_STOPPED = new ConcurrentHashMap<>();

    private static DLManager sManager;

    private Context context;

    private int maxTask = 10;

    private DLManager(Context context) {
        this.context = context;
    }

    public static DLManager getInstance(Context context) {
        if (null == sManager) {
            sManager = new DLManager(context);
        }
        return sManager;
    }

    public DLManager setMaxTask(int maxTask) {
        this.maxTask = maxTask;
        return sManager;
    }

    public DLManager setDebugEnable(boolean isDebug) {
        DLCons.DEBUG = isDebug;
        return sManager;
    }

    public void dlStart(String url, String dir, String name, List<DLHeader> headers, IDListener listener) {
        boolean hasListener = listener != null;
        if (TextUtils.isEmpty(url)) {
            if (hasListener) listener.onError(ERROR_INVALID_URL, "Url can not be null.");
            return;
        }
        if (!DLUtil.isNetworkAvailable(context)) {
            if (hasListener) listener.onError(ERROR_NOT_NETWORK, "Network is not available.");
            return;
        }
        if (TASK_DLING.containsKey(url)) {
            if (null != listener) listener.onError(ERROR_REPEAT_URL, url + " is downloading.");
        } else {
            DLInfo info;
            if (TASK_STOPPED.containsKey(url)) {
                if (DEBUG) Log.d(TAG, "Resume task from memory.");
                info = TASK_STOPPED.remove(url);
            } else {
                if (DEBUG) Log.d(TAG, "Resume task from database.");
                info = DLDBManager.getInstance(context).queryTaskInfo(url);
                if (null != info) {
                    info.threads.clear();
                    info.threads.addAll(DLDBManager.getInstance(context).queryAllThreadInfo(url));
                }
            }
            if (null == info) {
                if (DEBUG) Log.d(TAG, "New task will be start.");
                info = new DLInfo();
                info.baseUrl = url;
                info.realUrl = url;
                if (TextUtils.isEmpty(dir)) dir = context.getCacheDir().getAbsolutePath();
                info.dirPath = dir;
                info.fileName = name;
            } else {
                info.isResume = true;
                for (DLThreadInfo threadInfo : info.threads) {
                    threadInfo.isStop = false;
                }
            }
            info.redirect = 0;
            info.requestHeaders = DLUtil.initRequestHeaders(headers, info);
            info.listener = listener;
            info.hasListener = hasListener;
            if (TASK_DLING.size() >= maxTask) {
                if (DEBUG) Log.w(TAG, "Downloading urls is out of range.");
                TASK_PREPARE.add(info);
            } else {
                if (DEBUG) Log.d(TAG, "Prepare download from " + info.baseUrl);
                if (hasListener) listener.onPrepare();
                TASK_DLING.put(url, info);
                POOL_TASK.execute(new DLTask(context, info));
            }
        }
    }

    public void dlStop(String url) {
        if (TASK_DLING.containsKey(url)) {
            DLInfo info = TASK_DLING.get(url);
            if (!info.threads.isEmpty()) {
                for (DLThreadInfo threadInfo : info.threads) {
                    threadInfo.isStop = true;
                }
            }
        }
    }

    public void dlCancel(String url) {
        dlStop(url);
        DLInfo info;
        if (TASK_DLING.containsKey(url)) {
            info = TASK_DLING.get(url);
        } else {
            info = DLDBManager.getInstance(context).queryTaskInfo(url);
        }
        if (null != info) {
            File file = new File(info.dirPath, info.fileName);
            if (file.exists()) file.delete();
        }
        DLDBManager.getInstance(context).deleteTaskInfo(url);
        DLDBManager.getInstance(context).deleteAllThreadInfo(url);
    }

    synchronized DLManager removeDLTask(String url) {
        TASK_DLING.remove(url);
        return sManager;
    }

    synchronized DLManager addDLTask() {
        if (!TASK_PREPARE.isEmpty()) {
            POOL_TASK.execute(new DLTask(context, TASK_PREPARE.remove(0)));
        }
        return sManager;
    }

    synchronized DLManager addStopTask(DLInfo info) {
        TASK_STOPPED.put(info.baseUrl, info);
        return sManager;
    }

    synchronized DLManager addDLThread(DLThread thread) {
        POOL_Thread.execute(thread);
        return sManager;
    }

}