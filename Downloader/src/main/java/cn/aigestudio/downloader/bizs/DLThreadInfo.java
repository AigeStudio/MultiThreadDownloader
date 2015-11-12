package cn.aigestudio.downloader.bizs;

class DLThreadInfo {
    String id;
    int start, end;
    boolean isStop;

    DLThreadInfo(String id, int start, int end) {
        this.id = id;
        this.start = start;
        this.end = end;
    }
}