package cn.aigestudio.downloader.bizs;

import java.io.File;

/**
 * 任务实体类
 * Task entity.
 *
 * @author AigeStudio 2015-05-16
 * @author AigeStudio 2015-05-29
 * 修改构造方法
 */
class TaskInfo extends DLInfo {
    int progress, length;

    TaskInfo(File dlLocalFile, String baseUrl, String realUrl, int progress, int length) {
        super(dlLocalFile, baseUrl, realUrl);
        this.progress = progress;
        this.length = length;
    }
}