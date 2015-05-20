package cn.aigestudio.downloader.interfaces;

/**
 * 下载线程监听器
 * 该监听仅供下载线程使用
 *
 * @author AigeStudio 2015-05-16
 */
public interface IDLThreadListener {
    void onThreadProgress(int progress);
}
