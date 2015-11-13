package cn.aigestudio.downloader.bizs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.aigestudio.downloader.interfaces.IDListener;

/**
 * 下载实体类
 * Download entity.
 *
 * @author AigeStudio 2015-05-16
 */
class DLInfo {
    int redirect;
    int totalBytes;
    int currentBytes;
    boolean hasListener;
    boolean isResume;
    String id;
    String fileName;
    String dirPath;
    String baseUrl;
    String realUrl;
    String mimeType;
    String eTag;
    String disposition;
    String location;
    List<DLHeader> requestHeaders;
    final List<DLThreadInfo> threads;
    IDListener listener;
    File file;

    DLInfo() {
        id = UUID.randomUUID().toString();
        threads = new ArrayList<>();
    }

    synchronized void addDLThread(DLThreadInfo info) {
        threads.add(info);
    }

    synchronized void removeDLThread(DLThreadInfo info) {
        threads.remove(info);
    }
}