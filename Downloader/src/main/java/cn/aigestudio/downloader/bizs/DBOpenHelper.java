package cn.aigestudio.downloader.bizs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 打开数据库的帮助类
 * Database open helper
 *
 * @author AigeStudio 2015-05-08
 *         如果你想将表建立在自己的数据库直接将{@link #onCreate(android.database.sqlite.SQLiteDatabase)}和
 *         {@link #onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)}方法中的逻辑Copy至在你自己
 *         SQLiteOpenHelper类的onCreate和onUpgrade方法中
 *         You can use your own database just execute the {@link DLCons.DBCons#TB_TASK_SQL_CREATE} and
 *         {@link DLCons.DBCons#TB_THREAD_SQL_CREATE} in onCreate method which in your SQLiteOpenHelper,
 *         also the {@link DLCons.DBCons#TB_TASK_SQL_UPGRADE} and {@link DLCons.DBCons#TB_THREAD_SQL_UPGRADE} in onUpgrade.
 * @author AigeStudio 2015-05-29
 *         数据库版本升级
 *         Update database version.
 */
final class DBOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "dl.db";
    private static final int DB_VERSION = 2;

    DBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DLCons.DBCons.TB_TASK_SQL_CREATE);
        db.execSQL(DLCons.DBCons.TB_THREAD_SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DLCons.DBCons.TB_TASK_SQL_UPGRADE);
        db.execSQL(DLCons.DBCons.TB_THREAD_SQL_UPGRADE);
        onCreate(db);
    }
}