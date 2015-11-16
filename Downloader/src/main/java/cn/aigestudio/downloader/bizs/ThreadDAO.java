package cn.aigestudio.downloader.bizs;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import static cn.aigestudio.downloader.bizs.DLCons.DBCons.TB_THREAD;
import static cn.aigestudio.downloader.bizs.DLCons.DBCons.TB_THREAD_END;
import static cn.aigestudio.downloader.bizs.DLCons.DBCons.TB_THREAD_ID;
import static cn.aigestudio.downloader.bizs.DLCons.DBCons.TB_THREAD_START;
import static cn.aigestudio.downloader.bizs.DLCons.DBCons.TB_THREAD_URL_BASE;

class ThreadDAO implements IThreadDAO {
    private final DLDBHelper dbHelper;

    ThreadDAO(Context context) {
        dbHelper = new DLDBHelper(context);
    }

    @Override
    public void insertThreadInfo(DLThreadInfo info) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO " + TB_THREAD + "(" +
                        TB_THREAD_URL_BASE + ", " +
                        TB_THREAD_START + ", " +
                        TB_THREAD_END + ", " +
                        TB_THREAD_ID + ") VALUES (?,?,?,?)",
                new Object[]{info.baseUrl, info.start, info.end, info.id});
        db.close();
    }

    @Override
    public void deleteThreadInfo(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + TB_THREAD + " WHERE " + TB_THREAD_ID + "=?", new String[]{id});
        db.close();
    }

    @Override
    public void deleteAllThreadInfo(String url) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + TB_THREAD + " WHERE " + TB_THREAD_URL_BASE + "=?",
                new String[]{url});
        db.close();
    }

    @Override
    public void updateThreadInfo(DLThreadInfo info) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE " + TB_THREAD + " SET " +
                TB_THREAD_START + "=? WHERE " +
                TB_THREAD_URL_BASE + "=? AND " +
                TB_THREAD_ID + "=?", new Object[]{info.start, info.baseUrl, info.id});
        db.close();
    }

    @Override
    public DLThreadInfo queryThreadInfo(String id) {
        DLThreadInfo info = null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " +
                TB_THREAD_URL_BASE + ", " +
                TB_THREAD_START + ", " +
                TB_THREAD_END + " FROM " +
                TB_THREAD + " WHERE " +
                TB_THREAD_ID + "=?", new String[]{id});
        if (c.moveToFirst()) info = new DLThreadInfo(id, c.getString(0), c.getInt(1), c.getInt(2));
        c.close();
        db.close();
        return info;
    }

    @Override
    public List<DLThreadInfo> queryAllThreadInfo(String url) {
        List<DLThreadInfo> info = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " +
                TB_THREAD_URL_BASE + ", " +
                TB_THREAD_START + ", " +
                TB_THREAD_END + ", " +
                TB_THREAD_ID + " FROM " +
                TB_THREAD + " WHERE " +
                TB_THREAD_URL_BASE + "=?", new String[]{url});
        while (c.moveToNext())
            info.add(new DLThreadInfo(c.getString(3), c.getString(0), c.getInt(1), c.getInt(2)));
        c.close();
        db.close();
        return info;
    }
}