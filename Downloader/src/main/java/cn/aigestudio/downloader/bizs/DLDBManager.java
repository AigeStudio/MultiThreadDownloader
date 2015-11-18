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
        daoTask.insertTaskInfo(info);
    }

    @Override
    public synchronized void deleteTaskInfo(String url) {
        daoTask.deleteTaskInfo(url);
    }

    @Override
    public synchronized void updateTaskInfo(DLInfo info) {
        daoTask.updateTaskInfo(info);
    }

    @Override
    public synchronized DLInfo queryTaskInfo(String url) {
        return daoTask.queryTaskInfo(url);
    }

    @Override
    public synchronized void insertThreadInfo(DLThreadInfo info) {
        daoThread.insertThreadInfo(info);
    }

    @Override
    public synchronized void deleteThreadInfo(String id) {
        daoThread.deleteThreadInfo(id);
    }

    @Override
    public synchronized void deleteAllThreadInfo(String url) {
        daoThread.deleteAllThreadInfo(url);
    }

    @Override
    public synchronized void updateThreadInfo(DLThreadInfo info) {
        daoThread.updateThreadInfo(info);
    }

    @Override
    public synchronized DLThreadInfo queryThreadInfo(String id) {
        return daoThread.queryThreadInfo(id);
    }

    @Override
    public synchronized List<DLThreadInfo> queryAllThreadInfo(String url) {
        return daoThread.queryAllThreadInfo(url);
    }
}