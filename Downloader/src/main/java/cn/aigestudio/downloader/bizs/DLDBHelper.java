package cn.aigestudio.downloader.bizs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static cn.aigestudio.downloader.bizs.DLCons.DBCons.TB_TASK_SQL_CREATE;
import static cn.aigestudio.downloader.bizs.DLCons.DBCons.TB_TASK_SQL_UPGRADE;
import static cn.aigestudio.downloader.bizs.DLCons.DBCons.TB_THREAD_SQL_CREATE;
import static cn.aigestudio.downloader.bizs.DLCons.DBCons.TB_THREAD_SQL_UPGRADE;

final class DLDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "dl.db";
    private static final int DB_VERSION = 3;

    DLDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TB_TASK_SQL_CREATE);
        db.execSQL(TB_THREAD_SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TB_TASK_SQL_UPGRADE);
        db.execSQL(TB_THREAD_SQL_UPGRADE);
        onCreate(db);
    }
}