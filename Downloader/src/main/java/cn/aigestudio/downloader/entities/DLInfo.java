package cn.aigestudio.downloader.entities;

import java.io.File;
import java.io.Serializable;

/**
 * 下载实体类
 * Download entity.
 *
 * @author AigeStudio 2015-05-16
 */
public class DLInfo implements Serializable {
    public File dlLocalFile;
    public String baseUrl, realUrl;

    public DLInfo(File dlLocalFile, String baseUrl, String realUrl) {
        this.dlLocalFile = dlLocalFile;
        this.baseUrl = baseUrl;
        this.realUrl = realUrl;
    }
}
