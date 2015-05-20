package cn.aigestudio.downloader.interfaces;

import android.content.Context;

import cn.aigestudio.downloader.daos.DBOpenHelper;
import cn.aigestudio.downloader.entities.DLInfo;

/**
 * DAO抽象类
 * Abstract class of DAO.
 *
 * @author AigeStudio 2015-05-16
 */
public abstract class DAO {
    protected DBOpenHelper dbHelper;

    public DAO(Context context) {
        dbHelper = new DBOpenHelper(context);
    }

    public abstract void insertInfo(DLInfo info);

    public abstract void deleteInfo(String url);

    public abstract void updateInfo(DLInfo info);

    public abstract DLInfo queryInfo(String str);

    public void close() {
        dbHelper.close();
    }
}
