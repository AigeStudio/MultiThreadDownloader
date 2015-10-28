package cn.aigestudio.downloader.bizs;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 线程的DAO实现
 * DAO for thread.
 *
 * @author AigeStudio 2015-05-16
 * @author AigeStudio 2015-05-29
 *         根据域名重定向问题进行逻辑修改
 */
class ThreadDAO extends DLDAO {
    ThreadDAO(Context context) {
        super(context);
    }

    @Override
    void insertInfo(DLInfo info) {
        ThreadInfo i = (ThreadInfo) info;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO " + DLCons.DBCons.TB_THREAD + "(" +
                        DLCons.DBCons.TB_THREAD_URL_BASE + ", " +
                        DLCons.DBCons.TB_THREAD_URL_REAL + ", " +
                        DLCons.DBCons.TB_THREAD_FILE_PATH + ", " +
                        DLCons.DBCons.TB_THREAD_START + ", " +
                        DLCons.DBCons.TB_THREAD_END + ", " +
                        DLCons.DBCons.TB_THREAD_ID + ") VALUES (?,?,?,?,?,?)",
                new Object[]{i.baseUrl, i.realUrl, i.dlLocalFile.getAbsolutePath(), i.start,
                        i.end, i.id});
        db.close();
    }

    @Override
    void deleteInfo(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + DLCons.DBCons.TB_THREAD + " WHERE " +
                DLCons.DBCons.TB_THREAD_ID + "=?", new String[]{id});
        db.close();
    }

    void deleteInfos(String url) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + DLCons.DBCons.TB_THREAD + " WHERE " +
                DLCons.DBCons.TB_THREAD_URL_BASE + "=?", new String[]{url});
        db.close();
    }

    @Override
    void updateInfo(DLInfo info) {
        ThreadInfo i = (ThreadInfo) info;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE " + DLCons.DBCons.TB_THREAD + " SET " +
                DLCons.DBCons.TB_THREAD_START + "=? WHERE " +
                DLCons.DBCons.TB_THREAD_URL_BASE + "=? AND " +
                DLCons.DBCons.TB_THREAD_ID + "=?", new Object[]{i.start, i.baseUrl, i.id});
        db.close();
    }

    @Override
    DLInfo queryInfo(String id) {
        ThreadInfo info = null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " +
                DLCons.DBCons.TB_THREAD_URL_BASE + ", " +
                DLCons.DBCons.TB_THREAD_URL_REAL + ", " +
                DLCons.DBCons.TB_THREAD_FILE_PATH + ", " +
                DLCons.DBCons.TB_THREAD_START + ", " +
                DLCons.DBCons.TB_THREAD_END + " FROM " +
                DLCons.DBCons.TB_THREAD + " WHERE " +
                DLCons.DBCons.TB_THREAD_ID + "=?", new String[]{id});
        if (c.moveToFirst()) {
            info = new ThreadInfo(new File(c.getString(2)), c.getString(0), c.getString(1),
                    c.getInt(3), c.getInt(4), id);
        }
        c.close();
        db.close();
        return info;
    }

    List<ThreadInfo> queryInfos(String url) {
        List<ThreadInfo> infos = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " +
                DLCons.DBCons.TB_THREAD_URL_BASE + ", " +
                DLCons.DBCons.TB_THREAD_URL_REAL + ", " +
                DLCons.DBCons.TB_THREAD_FILE_PATH + ", " +
                DLCons.DBCons.TB_THREAD_START + ", " +
                DLCons.DBCons.TB_THREAD_END + ", " +
                DLCons.DBCons.TB_THREAD_ID + " FROM " +
                DLCons.DBCons.TB_THREAD + " WHERE " +
                DLCons.DBCons.TB_THREAD_URL_BASE + "=?", new String[]{url});
        while (c.moveToNext()) {
            infos.add(new ThreadInfo(new File(c.getString(2)), c.getString(0), c.getString(1),
                    c.getInt(3), c.getInt(4), c.getString(5)));
        }
        c.close();
        db.close();
        return infos;
    }
}
