package cn.aigestudio.downloader.bizs;

import android.content.Context;

/**
 * DAO抽象类
 * Abstract class of DAO.
 *
 * @author AigeStudio 2015-05-16
 */
abstract class DLDAO {
    protected DBOpenHelper dbHelper;

    DLDAO(Context context) {
        dbHelper = new DBOpenHelper(context);
    }

    abstract void insertInfo(DLInfo info);

    abstract void deleteInfo(String url);

    abstract void updateInfo(DLInfo info);

    abstract DLInfo queryInfo(String str);
}