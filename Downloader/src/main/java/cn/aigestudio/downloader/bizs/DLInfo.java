package cn.aigestudio.downloader.bizs;

import java.io.File;
import java.io.Serializable;

/**
 * 下载实体类
 * Download entity.
 *
 * @author AigeStudio 2015-05-16
 */
class DLInfo implements Serializable {
    File dlLocalFile;
    String baseUrl, realUrl;

    DLInfo(File dlLocalFile, String baseUrl, String realUrl) {
        this.dlLocalFile = dlLocalFile;
        this.baseUrl = baseUrl;
        this.realUrl = realUrl;
    }
}
