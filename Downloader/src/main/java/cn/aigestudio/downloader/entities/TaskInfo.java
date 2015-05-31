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
    public int progress, length;

    public TaskInfo(File dlLocalFile, String baseUrl, String realUrl, int progress, int length) {
        super(dlLocalFile, baseUrl, realUrl);
        this.progress = progress;
        this.length = length;
    }
}
