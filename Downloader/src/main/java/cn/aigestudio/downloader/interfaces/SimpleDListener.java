package cn.aigestudio.downloader.interfaces;

import java.io.File;

public class SimpleDListener implements IDListener {

    @Override
    public void onPrepare() {

    }

    @Override
    public void onStart(String fileName, String realUrl, int fileLength) {

    }

    @Override
    public void onProgress(int progress) {

    }

    @Override
    public void onStop(int progress) {

    }

    @Override
    public void onFinish(File file) {

    }

    @Override
    public void onError(int status, String error) {

    }
}