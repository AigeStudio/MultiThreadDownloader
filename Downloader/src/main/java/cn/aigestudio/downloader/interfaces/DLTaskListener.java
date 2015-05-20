package cn.aigestudio.downloader.interfaces;

import java.io.File;

/**
 * 下载监听器
 * Download listener.
 *
 * @author AigeStudio 2015-05-08
 */
public class DLTaskListener {
    /**
     * 下载开始时回调 暂未使用
     * Callback when download start. No use.
     *
     * @param length 文件字节长度 File length in byte.
     */
//    public void onStart(int length) {
//
//    }

    /**
     * 网络连接时回调
     * Callback when connect the network.
     *
     * @param type 具体的网络类型{@link cn.aigestudio.downloader.cons.PublicCons.NetType} type of network
     * @param msg  附加的连接信息 extra message of connect.
     * @return true表示连接正常 否则反之 true if connect success, otherwise is return false.
     */
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

    /**
     * 下载停止时回调 暂未使用
     * Callback when download stop. No use.
     */
//    public void onStop() {
//
//    }

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
    public void onError(String error) {
    }
}
