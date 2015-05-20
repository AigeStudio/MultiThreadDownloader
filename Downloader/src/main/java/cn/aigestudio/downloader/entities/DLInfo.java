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
    public String url;

    public DLInfo(File dlLocalFile, String url) {
        this.dlLocalFile = dlLocalFile;
        this.url = url;
    }
}
