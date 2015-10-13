package cn.aigestudio.downloader.entities;

import java.io.File;
import java.io.Serializable;

/**
 * 任务实体类
 * Task entity.
 *
 * @author AigeStudio 2015-05-16
 * @author AigeStudio 2015-05-29
 * 修改构造方法
 */
public class TaskInfo extends DLInfo implements Serializable {
    public int progress, length,threadNum;

    public TaskInfo(File dlLocalFile, String baseUrl, String realUrl, int progress, int length) {
        this(dlLocalFile, baseUrl, realUrl, progress,length,3);
    }

    public TaskInfo(File dlLocalFile, String baseUrl, String realUrl, int progress, int length, int threadNum) {
        super(dlLocalFile, baseUrl, realUrl);
        this.progress = progress;
        this.length = length;
        this.threadNum = threadNum;
    }
}
