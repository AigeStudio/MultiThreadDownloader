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
 *
 * @author AigeStudio 2015-05-08
 */
public final class DBOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "dl.db";// 数据库名
    private static final int DB_VERSION = 1;// 数据库版本号

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建下载任务表
        db.execSQL(PublicCons.DBCons.TB_TASK_SQL_CREATE);

        // 创建下载任务线程表
        db.execSQL(PublicCons.DBCons.TB_THREAD_SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 删除下载任务表
        db.execSQL(PublicCons.DBCons.TB_TASK_SQL_UPGRADE);

        // 删除下载任务线程表
        db.execSQL(PublicCons.DBCons.TB_THREAD_SQL_UPGRADE);

        // 重建表信息
        onCreate(db);
    }
}
