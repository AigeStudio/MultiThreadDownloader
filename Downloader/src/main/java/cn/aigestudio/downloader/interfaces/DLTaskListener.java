package cn.aigestudio.downloader.interfaces;

import java.io.File;

/**
 * 下载监听器
 *
 * @author AigeStudio 2015-05-08
 */
public class DLTaskListener {
    /**
     * 下载开始时回调
     *
     * @param length 文件字节长度
     */
    public void onStart(int length) {

    }

    /**
     * 网络连接时回调
     *
     * @param type 具体的网络类型{@link cn.aigestudio.downloader.cons.PublicCons.NetType}
     * @param msg  附加的连接信息
     * @return true表示连接正常 否则反之
     */
    public boolean onConnect(int type, String msg) {
        return true;
    }

    /**
     * 下载进行时回调
     *
     * @param progress 当前的下载进度以100为最大单位
     */
    public void onProgress(int progress) {

    }

    /**
     * 下载停止时回调
     */
    public void onStop() {

    }

    /**
     * 下载完成时回调
     *
     * @param file 下载文件本地File对象
     */
    public void onFinish(File file) {

    }

    /**
     * 下载出错时回调
     *
     * @param error 具体的错误信息
     */
    public void onError(String error) {
    }
}
