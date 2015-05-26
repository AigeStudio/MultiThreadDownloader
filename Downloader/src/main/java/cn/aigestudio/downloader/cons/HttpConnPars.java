package cn.aigestudio.downloader.cons;

/**
 * HTTP参数枚举类
 * HTTP Parameters.
 *
 * @author AigeStudio 2015-05-08
 */
public enum HttpConnPars {
    POST("GET"),
    ACCECT("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*"),
    ACCECT_LANGAGE("Accept-Language", "zh-CN"),
    CHARSET("Charset", "UTF-8"),
    CONNECTTIEMEDOUT("5000"),
    KEEPCONNECT("Connection", "Keep-Alive"),
    LOCATION("location"),
    REFERER("referer");

    public String header;
    public String content;

    private HttpConnPars(String header, String content) {
        this.header = header;
        this.content = content;
    }

    private HttpConnPars(String content) {
        this.content = content;
    }
}
