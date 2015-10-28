package cn.aigestudio.downloader.bizs;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * 下载任务的DAO实现
 * DAO for download task.
 *
 * @author AigeStudio 2015-05-09
 * @author AigeStudio 2015-05-29
 *         根据域名重定向问题进行逻辑修改
 */
class TaskDAO extends DLDAO {
    TaskDAO(Context context) {
        super(context);
    }

    @Override
    void insertInfo(DLInfo info) {
        TaskInfo i = (TaskInfo) info;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO " + DLCons.DBCons.TB_TASK + "(" +
                        DLCons.DBCons.TB_TASK_URL_BASE + ", " +
                        DLCons.DBCons.TB_TASK_URL_REAL + ", " +
                        DLCons.DBCons.TB_TASK_FILE_PATH + ", " +
                        DLCons.DBCons.TB_TASK_PROGRESS + ", " +
                        DLCons.DBCons.TB_TASK_FILE_LENGTH + ") values (?,?,?,?,?)",
                new Object[]{i.baseUrl, i.realUrl, i.dlLocalFile.getAbsolutePath(), i.progress,
                        i.length});
        db.close();
    }

    @Override
    void deleteInfo(String url) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + DLCons.DBCons.TB_TASK + " WHERE " +
                DLCons.DBCons.TB_TASK_URL_BASE + "=?", new String[]{url});
        db.close();
    }

    @Override
    void updateInfo(DLInfo info) {
        TaskInfo i = (TaskInfo) info;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE " + DLCons.DBCons.TB_TASK + " SET " +
                DLCons.DBCons.TB_TASK_PROGRESS + "=? WHERE " +
                DLCons.DBCons.TB_TASK_URL_BASE + "=?", new Object[]{i.progress, i.baseUrl});
        db.close();
    }

    @Override
    DLInfo queryInfo(String url) {
        TaskInfo info = null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " +
                DLCons.DBCons.TB_TASK_URL_BASE + ", " +
                DLCons.DBCons.TB_TASK_URL_REAL + ", " +
                DLCons.DBCons.TB_TASK_FILE_PATH + ", " +
                DLCons.DBCons.TB_TASK_PROGRESS + ", " +
                DLCons.DBCons.TB_TASK_FILE_LENGTH + " FROM " +
                DLCons.DBCons.TB_TASK + " WHERE " +
                DLCons.DBCons.TB_TASK_URL_BASE + "=?", new String[]{url});
        if (c.moveToFirst()) {
            info = new TaskInfo(new File(c.getString(2)), c.getString(0), c.getString(1),
                    c.getInt(3), c.getInt(4));
        }
        c.close();
        db.close();
        return info;
    }
}
