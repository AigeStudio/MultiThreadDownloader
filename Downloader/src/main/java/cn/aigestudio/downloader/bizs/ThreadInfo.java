package cn.aigestudio.downloader.bizs;

import java.io.File;
import java.io.Serializable;

/**
 * 线程实体类
 * Thread entity.
 *
 * @author AigeStudio 2015-05-16
 * @author AigeStudio 2015-05-29
 *         修改构造方法
 */
class ThreadInfo extends DLInfo implements Serializable {
    String id;
    int start, end;

    ThreadInfo(File dlLocalFile, String baseUrl, String realUrl, int start, int end, String id) {
        super(dlLocalFile, baseUrl, realUrl);
        this.start = start;
        this.end = end;
        this.id = id;
    }
}
