package cn.aigestudio.downloader.bizs;

interface IDLThreadListener {
    void onProgress(int progress);

    void onStop();

    void onFinish(DLThreadInfo threadInfo);
}