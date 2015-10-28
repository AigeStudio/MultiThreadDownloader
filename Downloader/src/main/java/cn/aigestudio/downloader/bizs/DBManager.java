package cn.aigestudio.downloader.bizs;

import android.content.Context;

import java.util.List;

/**
 * 数据库管理器
 * 封装各种业务数据操作
 * DataBase manager
 *
 * @author AigeStudio 2015-05-09
 */
 final class DBManager {
    private static DBManager sManager;

    private TaskDAO daoTask;
    private ThreadDAO daoThread;

    private DBManager(Context context) {
        daoTask = new TaskDAO(context);
        daoThread = new ThreadDAO(context);
    }

    /**
     * 获取数据库管理器单例对象
     *
     * @param context ...
     * @return 数据库管理器单例对象
     */
     static DBManager getInstance(Context context) {
        if (null == sManager) {
            sManager = new DBManager(context);
        }
        return sManager;
    }

    /**
     * 插入一条下载任务数据信息
     *
     * @param info 下载任务对象
     */
     synchronized void insertTaskInfo(TaskInfo info) {
        daoTask.insertInfo(info);
    }

    /**
     * 根据下载地址删除一条下载任务数据信息
     *
     * @param url 下载地址
     */
     synchronized void deleteTaskInfo(String url) {
        daoTask.deleteInfo(url);
    }

    /**
     * 更新一条下载任务数据信息
     *
     * @param info 下载任务对象
     */
     synchronized void updateTaskInfo(TaskInfo info) {
        daoTask.updateInfo(info);
    }

    /**
     * 根据下载地址查询一条下载任务数据信息
     *
     * @param url 下载地址
     * @return 下载任务对象
     */
     synchronized TaskInfo queryTaskInfoByUrl(String url) {
        return (TaskInfo) daoTask.queryInfo(url);
    }

    /**
     * 插入一条线程数据信息
     *
     * @param info 线程对象
     */
     synchronized void insertThreadInfo(ThreadInfo info) {
        daoThread.insertInfo(info);
    }

    /**
     * 根据线程ID删除一条线程数据信息
     *
     * @param id 线程ID
     */
     synchronized void deleteThreadInfoById(String id) {
        daoThread.deleteInfo(id);
    }

    /**
     * 根据下载地址删除所有线程数据信息
     *
     * @param url 下载地址
     */
     synchronized void deleteThreadInfos(String url) {
        daoThread.deleteInfo(url);
    }

    /**
     * 更新一条线程数据信息
     *
     * @param info 线程对象
     */
     synchronized void updateThreadInfo(ThreadInfo info) {
        daoThread.updateInfo(info);
    }

    /**
     * 根据线程ID查询一条线程数据信息
     *
     * @param id 线程ID
     * @return 线程对象
     */
     synchronized ThreadInfo queryThreadInfoById(String id) {
        return (ThreadInfo) daoThread.queryInfo(id);
    }

    /**
     * 根据下载地址查询所有线程数据信息
     *
     * @param url 下载地址
     * @return 所有该地址下对应的线程信息
     */
     synchronized List<ThreadInfo> queryThreadInfos(String url) {
        return daoThread.queryInfos(url);
    }
}