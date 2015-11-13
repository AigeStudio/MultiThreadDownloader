package cn.aigestudio.downloader.bizs;

import java.util.List;

interface IThreadDAO {
    void insertThreadInfo(DLThreadInfo info);

    void deleteThreadInfo(String url);

    void updateThreadInfo(DLThreadInfo info);

    DLThread queryThreadInfo(String id);

    List<DLThread> queryAllThreadInfo(String url);
}