package cn.aigestudio.downloader.bizs;

import android.provider.BaseColumns;

/**
 * 公共常量
 * Public constants.
 *
 * @author AigeStudio 2015-05-08
 */
final class DLCons {
    /**
     *
     */
    static boolean DEBUG = true;

    /**
     * 文件访问模式
     * File access mode.
     *
     * @author AigeStudio 2015-05-08
     */
    static final class AccessModes {
        static final String ACCESS_MODE_R = "r";
        static final String ACCESS_MODE_RW = "rw";
        static final String ACCESS_MODE_RWS = "rws";
        static final String ACCESS_MODE_RWD = "rwd";
    }

    /**
     * 数据库常量
     * Database constants.
     *
     * @author AigeStudio 2015-05-08
     * @author AigeStudio 2015-05-29
     *         更改常量TB_TASK_URL为TB_TASK_URL_BASE
     *         新增常量TB_TASK_URL_REAL
     *         更改常量TB_THREAD_URL为TB_THREAD_URL_BASE
     *         新增常量TB_THREAD_URL_REAL
     */
    static final class DBCons {
        static final String TB_TASK = "task_info";
        static final String TB_TASK_URL_BASE = "base_url";
        static final String TB_TASK_URL_REAL = "real_url";
        static final String TB_TASK_FILE_PATH = "file_path";
        static final String TB_TASK_PROGRESS = "onThreadProgress";
        static final String TB_TASK_FILE_LENGTH = "file_length";

        static final String TB_THREAD = "thread_info";
        static final String TB_THREAD_URL_BASE = "base_url";
        static final String TB_THREAD_URL_REAL = "real_url";
        static final String TB_THREAD_FILE_PATH = "file_path";
        static final String TB_THREAD_START = "start";
        static final String TB_THREAD_END = "end";
        static final String TB_THREAD_ID = "id";

        static final String TB_TASK_SQL_CREATE = "CREATE TABLE " +
                DLCons.DBCons.TB_TASK + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DLCons.DBCons.TB_TASK_URL_BASE + " CHAR, " +
                DLCons.DBCons.TB_TASK_URL_REAL + " CHAR, " +
                DLCons.DBCons.TB_TASK_FILE_PATH + " CHAR, " +
                DLCons.DBCons.TB_TASK_PROGRESS + " INTEGER, " +
                DLCons.DBCons.TB_TASK_FILE_LENGTH + " INTEGER)";
        static final String TB_THREAD_SQL_CREATE = "CREATE TABLE " +
                DLCons.DBCons.TB_THREAD + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DLCons.DBCons.TB_THREAD_URL_BASE + " CHAR, " +
                DLCons.DBCons.TB_THREAD_URL_REAL + " CHAR, " +
                DLCons.DBCons.TB_THREAD_FILE_PATH + " CHAR, " +
                DLCons.DBCons.TB_THREAD_START + " INTEGER, " +
                DLCons.DBCons.TB_THREAD_END + " INTEGER, " +
                DLCons.DBCons.TB_THREAD_ID + " CHAR)";

        static final String TB_TASK_SQL_UPGRADE = "DROP TABLE IF EXISTS " +
                DLCons.DBCons.TB_TASK;
        static final String TB_THREAD_SQL_UPGRADE = "DROP TABLE IF EXISTS " +
                DLCons.DBCons.TB_THREAD;
    }
}