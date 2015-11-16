package cn.aigestudio.downloader.bizs;

import java.util.List;

interface IThreadDAO {
    void insertThreadInfo(DLThreadInfo info);

    void deleteThreadInfo(String id);

    void deleteAllThreadInfo(String url);

    void updateThreadInfo(DLThreadInfo info);

    DLThreadInfo queryThreadInfo(String id);

    List<DLThreadInfo> queryAllThreadInfo(String url);
}