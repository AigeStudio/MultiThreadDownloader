package cn.aigestudio.downloader.interfaces;

import java.io.File;

/**
 * 下载监听器
 * Download listener.
 *
 * @author AigeStudio 2015-05-08
 */
@Deprecated
public class DLTaskListener implements IDListener {
    @Override
    public void onPrepare() {

    }

    /**
     * 下载开始时回调 暂未使用
     * Callback when download start. No use.
     *
     * @param fileName 文件名 File name.
     * @param url      文件下载地址 File length in byte.
     */
    @Deprecated
    public void onStart(String fileName, String url) {
    }

    @Override
    public void onStart(String fileName, String realUrl, int fileLength) {
        onStart(fileName, realUrl);
    }

    /**
     * 网络连接时回调
     * Callback when connect the network.
     *
     * @param msg 附加的连接信息 extra message of connect.
     * @return true表示连接正常 否则反之 true if connect success, otherwise is return false.
     */
    @Deprecated
    public boolean onConnect(int type, String msg) {
        return true;
    }

    /**
     * 下载进行时回调
     * Callback when download in progress.
     *
     * @param progress 当前的下载进度以100为最大单位 note:the max progress is 100.
     */
    public void onProgress(int progress) {

    }

    @Deprecated
    public void onStop() {

    }

    /**
     * 下载停止时回调 暂未使用
     * Callback when download stop. No use.
     */
    public void onStop(int progress) {
        onStop();
    }

    /**
     * 下载完成时回调
     * Callback when download finish.
     *
     * @param file 下载文件本地File对象 file downloaded.
     */
    public void onFinish(File file) {

    }

    /**
     * 下载出错时回调
     * Callback when download error.
     *
     * @param error 具体的错误信息 error message.
     */
    @Deprecated
    public void onError(String error) {
    }

    @Override
    public void onError(int status, String error) {
        onError(error);
    }
}
