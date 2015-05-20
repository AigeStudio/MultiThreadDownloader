package cn.aigestudio.downloader.cons;

/**
 * HTTP参数枚举类
 *
 * @author AigeStudio 2015-05-08
 */
public enum HttpConnPars {
    // 请求方式
    POST("GET"),

    // 请求格式
    ACCECT("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*"),

    // 请求语言
    ACCECT_LANGAGE("Accept-Language", "zh-CN"),

    // 请求的字符编码
    CHARSET("Charset", "UTF-8"),

    // 链接的超时数
    CONNECTTIEMEDOUT("5000"),

    // 保持链接
    KEEPCONNECT("Connection", "Keep-Alive");

    public String header;// 标题
    public String content;// 内容

    private HttpConnPars(String header, String content) {
        this.header = header;
        this.content = content;
    }

    private HttpConnPars(String content) {
        this.content = content;
    }
}
