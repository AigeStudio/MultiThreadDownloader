package cn.aigestudio.downloader.entities;

import java.io.File;
import java.io.Serializable;

/**
 * 线程实体类
 *
 * @author AigeStudio 2015-05-16
 */
public class ThreadInfo extends DLInfo implements Serializable {
    public String id;
    public int start, end;

    public ThreadInfo(File dlLocalFile, String url, int start, int end, String id) {
        super(dlLocalFile, url);
        this.start = start;
        this.end = end;
        this.id = id;
    }
}
