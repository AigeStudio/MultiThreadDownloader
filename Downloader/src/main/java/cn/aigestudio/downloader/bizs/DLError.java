package cn.aigestudio.downloader.bizs;

public final class DLError {
    private DLError() {
    }

    /**
     * 没有网络
     */
    public static final int ERROR_NOT_NETWORK = 0;
    /**
     * 创建文件失败
     */
    public static final int ERROR_CREATE_FILE = 1;
    /**
     * 无效Url
     */
    public static final int ERROR_INVALID_URL = 2;
    /**
     * 重复的下载地址
     */
    public static final int ERROR_REPEAT_URL = 101;
    /**
     * 无法获取真实下载地址
     */
    public static final int ERROR_CANNOT_GET_URL = 137;
    /**
     * 建立连接出错
     */
    public static final int ERROR_OPEN_CONNECT = 138;
    /**
     * 未能处理的重定向错误
     */
    public static final int ERROR_UNHANDLED_REDIRECT = 333;
}