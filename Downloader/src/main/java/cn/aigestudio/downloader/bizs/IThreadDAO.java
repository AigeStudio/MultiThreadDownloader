package cn.aigestudio.downloader.bizs;

import java.util.List;

interface IThreadDAO {
    void insertThreadInfo(DLThread info);

    void deleteThreadInfo(String url);

    void updateThreadInfo(DLThread info);

    DLThread queryThreadInfo(String id);

    List<DLThread> queryAllThreadInfo(String url);
}