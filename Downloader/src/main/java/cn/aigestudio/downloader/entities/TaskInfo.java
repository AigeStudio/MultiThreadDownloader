package cn.aigestudio.downloader.entities;

import java.io.File;
import java.io.Serializable;

/**
 * 任务实体类
 *
 * @author AigeStudio 2015-05-16
 */
public class TaskInfo extends DLInfo implements Serializable {
    public int progress, length;

    public TaskInfo(File dlLocalFile, String url, int progress, int length) {
        super(dlLocalFile, url);
        this.progress = progress;
        this.length = length;
    }
}
