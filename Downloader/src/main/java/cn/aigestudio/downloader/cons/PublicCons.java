package cn.aigestudio.downloader.cons;

import android.provider.BaseColumns;

/**
 * 公共常量
 *
 * @author AigeStudio 2015-05-08
 */
public final class PublicCons {
    /**
     * 文件访问模式
     *
     * @author AigeStudio 2015-05-08
     */
    public static final class AccessModes {
        public static final String ACCESS_MODE_R = "r";
        public static final String ACCESS_MODE_RW = "rw";
        public static final String ACCESS_MODE_RWS = "rws";
        public static final String ACCESS_MODE_RWD = "rwd";
    }

    /**
     * 数据库常量
     *
     * @author AigeStudio 2015-05-08
     */
    public static final class DBCons {
        public static final String TB_TASK = "task_info";// 下载任务表
        public static final String TB_TASK_URL = "url";// 下载路径
        public static final String TB_TASK_FILE_PATH = "file_path";// 下载文件本地保存路径
        public static final String TB_TASK_PROGRESS = "onThreadProgress";// 下载进度
        public static final String TB_TASK_FILE_LENGTH = "file_length";// 下载文件的长度

        public static final String TB_THREAD = "thread_info";// 下载任务线程表
        public static final String TB_THREAD_URL = "url";// 下载路径
        public static final String TB_THREAD_FILE_PATH = "file_path";// 下载文件本地保存路径
        public static final String TB_THREAD_START = "start";// 从哪个字节开始下载
        public static final String TB_THREAD_END = "end";// 到哪个字节结束下载
        public static final String TB_THREAD_ID = "id";// 下载线程的ID

        public static final String TB_TASK_SQL_CREATE = "CREATE TABLE " +
                PublicCons.DBCons.TB_TASK + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PublicCons.DBCons.TB_TASK_URL + " CHAR, " +
                PublicCons.DBCons.TB_TASK_FILE_PATH + " CHAR, " +
                PublicCons.DBCons.TB_TASK_PROGRESS + " INTEGER, " +
                PublicCons.DBCons.TB_TASK_FILE_LENGTH + " INTEGER)";// 创建下载任务表的SQL语句
        public static final String TB_THREAD_SQL_CREATE = "CREATE TABLE " +
                PublicCons.DBCons.TB_THREAD + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PublicCons.DBCons.TB_THREAD_URL + " CHAR, " +
                PublicCons.DBCons.TB_THREAD_FILE_PATH + " CHAR, " +
                PublicCons.DBCons.TB_THREAD_START + " INTEGER, " +
                PublicCons.DBCons.TB_THREAD_END + " INTEGER, " +
                PublicCons.DBCons.TB_THREAD_ID + " CHAR)";// 创建下载任务线程表的SQL语句

        public static final String TB_TASK_SQL_UPGRADE = "DROP TABLE IF EXISTS " +
                PublicCons.DBCons.TB_TASK;// 删除下载任务表的SQL语句
        public static final String TB_THREAD_SQL_UPGRADE = "DROP TABLE IF EXISTS " +
                PublicCons.DBCons.TB_THREAD;// 删除下载任务表的SQL语句
    }

    /**
     * 网络类型
     *
     * @author AigeStudio 2015-05-08
     */
    public static final class NetType {
        public static final int INVALID = 0;// 没有网络
        public static final int WAP = 1;// WAP网络
        public static final int G2 = 2;// 2G网络
        public static final int G3 = 3;// 3G+网络
        public static final int WIFI = 4;// WIFI网络
        public static final int NO_WIFI = 5;// 非WIFI网络
    }
}
