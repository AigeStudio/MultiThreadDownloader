package cn.aigestudio.downloader.bizs;

import android.content.Context;

import java.util.List;

final class DLDBManager implements ITaskDAO, IThreadDAO {
    private static DLDBManager sManager;

    private TaskDAO daoTask;
    private ThreadDAO daoThread;

    private DLDBManager(Context context) {
        daoTask = new TaskDAO(context);
        daoThread = new ThreadDAO(context);
    }

    static DLDBManager getInstance(Context context) {
        if (null == sManager) {
            sManager = new DLDBManager(context);
        }
        return sManager;
    }

    @Override
    public synchronized void insertTaskInfo(DLInfo info) {

    }

    @Override
    public synchronized void deleteTaskInfo(String url) {

    }

    @Override
    public synchronized void updateTaskInfo(DLInfo info) {

    }

    @Override
    public synchronized DLInfo queryTaskInfo(String url) {
        return null;
    }

    @Override
    public synchronized void insertThreadInfo(DLThreadInfo info) {

    }

    @Override
    public synchronized void deleteThreadInfo(String url) {

    }

    @Override
    public synchronized void updateThreadInfo(DLThreadInfo info) {

    }

    @Override
    public synchronized DLThread queryThreadInfo(String id) {
        return null;
    }

    @Override
    public synchronized List<DLThread> queryAllThreadInfo(String url) {
        return null;
    }
}