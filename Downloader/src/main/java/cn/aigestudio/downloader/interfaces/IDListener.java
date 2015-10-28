package cn.aigestudio.downloader.interfaces;

import java.io.File;

/**
 * @author AigeStudio 2015-10-18
 */
public interface IDListener {
    void onPrepare();

    void onStart(String fileName, String realUrl, int fileLength);

    void onProgress(int progress);

    void onStop(int progress);

    void onFinish(File file);

    void onError(int status, String error);
}