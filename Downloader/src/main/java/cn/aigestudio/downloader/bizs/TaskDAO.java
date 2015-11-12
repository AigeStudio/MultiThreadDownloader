package cn.aigestudio.downloader.bizs;

import android.content.Context;

class TaskDAO implements ITaskDAO {
    private final DLDBHelper dbHelper;

    TaskDAO(Context context) {
        dbHelper = new DLDBHelper(context);
    }

    @Override
    public void insertTaskInfo(DLInfo info) {

    }

    @Override
    public void deleteTaskInfo(String url) {

    }

    @Override
    public void updateTaskInfo(DLInfo info) {

    }

    @Override
    public DLInfo queryTaskInfo(String url) {
        return null;
    }
}