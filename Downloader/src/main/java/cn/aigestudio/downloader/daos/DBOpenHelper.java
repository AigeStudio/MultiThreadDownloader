package cn.aigestudio.downloader.daos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.aigestudio.downloader.cons.PublicCons;

/**
 * 打开数据库的帮助类
 * 如果你想将表建立在自己的数据库直接将{@link #onCreate(android.database.sqlite.SQLiteDatabase)}和
 * {@link #onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)}方法中的逻辑Copy至在你自己
 * SQLiteOpenHelper类的onCreate和onUpgrade方法中
 * Database open helper
 * You can use your own database just execute the {@link PublicCons.DBCons#TB_TASK_SQL_CREATE} and
 * {@link PublicCons.DBCons#TB_THREAD_SQL_CREATE} in onCreate method which in your SQLiteOpenHelper,
 * also the {@link PublicCons.DBCons#TB_TASK_SQL_UPGRADE} and {@link PublicCons.DBCons#TB_THREAD_SQL_UPGRADE} in onUpgrade.
 *
 * @author AigeStudio 2015-05-08
 */
public final class DBOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "dl.db";
    private static final int DB_VERSION = 1;

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PublicCons.DBCons.TB_TASK_SQL_CREATE);
        db.execSQL(PublicCons.DBCons.TB_THREAD_SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(PublicCons.DBCons.TB_TASK_SQL_UPGRADE);
        db.execSQL(PublicCons.DBCons.TB_THREAD_SQL_UPGRADE);
        onCreate(db);
    }
}
