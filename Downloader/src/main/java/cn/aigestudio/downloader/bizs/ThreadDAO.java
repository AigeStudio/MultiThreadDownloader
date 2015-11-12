package cn.aigestudio.downloader.bizs;

import android.content.Context;

import java.util.List;

class ThreadDAO implements IThreadDAO {
    private final DLDBHelper dbHelper;

    ThreadDAO(Context context) {
        dbHelper = new DLDBHelper(context);
    }

    @Override
    public void insertThreadInfo(DLThread info) {

    }

    @Override
    public void deleteThreadInfo(String url) {

    }

    @Override
    public void updateThreadInfo(DLThread info) {

    }

    @Override
    public DLThread queryThreadInfo(String id) {
        return null;
    }

    @Override
    public List<DLThread> queryAllThreadInfo(String url) {
        return null;
    }
}