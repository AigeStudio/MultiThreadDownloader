package cn.aigestudio.downloader.daos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.aigestudio.downloader.cons.PublicCons;
import cn.aigestudio.downloader.entities.DLInfo;
import cn.aigestudio.downloader.entities.ThreadInfo;
import cn.aigestudio.downloader.interfaces.DAO;

/**
 * 线程的DAO实现
 * DAO for thread.
 *
 * @author AigeStudio 2015-05-16
 * @author AigeStudio 2015-05-29
 *         根据域名重定向问题进行逻辑修改
 */
public class ThreadDAO extends DAO {
    public ThreadDAO(Context context) {
        super(context);
    }

    @Override
    public void insertInfo(DLInfo info) {
        ThreadInfo i = (ThreadInfo) info;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO " + PublicCons.DBCons.TB_THREAD + "(" +
                        PublicCons.DBCons.TB_THREAD_URL_BASE + ", " +
                        PublicCons.DBCons.TB_THREAD_URL_REAL + ", " +
                        PublicCons.DBCons.TB_THREAD_FILE_PATH + ", " +
                        PublicCons.DBCons.TB_THREAD_START + ", " +
                        PublicCons.DBCons.TB_THREAD_END + ", " +
                        PublicCons.DBCons.TB_THREAD_ID + ") VALUES (?,?,?,?,?,?)",
                new Object[]{i.baseUrl, i.realUrl, i.dlLocalFile.getAbsolutePath(), i.start,
                        i.end, i.id});
        db.close();
    }

    @Override
    public void deleteInfo(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + PublicCons.DBCons.TB_THREAD + " WHERE " +
                PublicCons.DBCons.TB_THREAD_ID + "=?", new String[]{id});
        db.close();
    }

    public void deleteInfos(String url) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + PublicCons.DBCons.TB_THREAD + " WHERE " +
                PublicCons.DBCons.TB_THREAD_URL_BASE + "=?", new String[]{url});
        db.close();
    }

    @Override
    public void updateInfo(DLInfo info) {
        ThreadInfo i = (ThreadInfo) info;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE " + PublicCons.DBCons.TB_THREAD + " SET " +
                PublicCons.DBCons.TB_THREAD_START + "=? WHERE " +
                PublicCons.DBCons.TB_THREAD_URL_BASE + "=? AND " +
                PublicCons.DBCons.TB_THREAD_ID + "=?", new Object[]{i.start, i.baseUrl, i.id});
        db.close();
    }

    @Override
    public DLInfo queryInfo(String id) {
        ThreadInfo info = null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " +
                PublicCons.DBCons.TB_THREAD_URL_BASE + ", " +
                PublicCons.DBCons.TB_THREAD_URL_REAL + ", " +
                PublicCons.DBCons.TB_THREAD_FILE_PATH + ", " +
                PublicCons.DBCons.TB_THREAD_START + ", " +
                PublicCons.DBCons.TB_THREAD_END + " FROM " +
                PublicCons.DBCons.TB_THREAD + " WHERE " +
                PublicCons.DBCons.TB_THREAD_ID + "=?", new String[]{id});
        if (c.moveToFirst()) {
            info = new ThreadInfo(new File(c.getString(2)), c.getString(0), c.getString(1),
                    c.getInt(3), c.getInt(4), id);
        }
        c.close();
        db.close();
        return info;
    }

    public List<ThreadInfo> queryInfos(String url) {
        List<ThreadInfo> infos = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " +
                PublicCons.DBCons.TB_THREAD_URL_BASE + ", " +
                PublicCons.DBCons.TB_THREAD_URL_REAL + ", " +
                PublicCons.DBCons.TB_THREAD_FILE_PATH + ", " +
                PublicCons.DBCons.TB_THREAD_START + ", " +
                PublicCons.DBCons.TB_THREAD_END + ", " +
                PublicCons.DBCons.TB_THREAD_ID + " FROM " +
                PublicCons.DBCons.TB_THREAD + " WHERE " +
                PublicCons.DBCons.TB_THREAD_URL_BASE + "=?", new String[]{url});
        while (c.moveToNext()) {
            infos.add(new ThreadInfo(new File(c.getString(2)), c.getString(0),c.getString(1),
                    c.getInt(3),  c.getInt(4), c.getString(5)));
        }
        c.close();
        db.close();
        return infos;
    }
}
