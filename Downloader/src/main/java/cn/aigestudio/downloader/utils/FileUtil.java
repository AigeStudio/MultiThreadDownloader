package cn.aigestudio.downloader.utils;

import java.io.File;
import java.io.IOException;

/**
 * 文件操作工具类
 *
 * @author AigeStudio 2015-05-08
 */
public final class FileUtil {
    /**
     * 根据URL路径获取文件名
     *
     * @param url URL路径
     * @return 文件名
     */
    public static String getFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/"));
    }

    /**
     * 创建文件夹
     *
     * @param path 文件夹路径
     * @return 创建了的文件夹File对象
     */
    public static File makeDir(String path) {
        File dir = new File(path);
        if (!isExist(dir)) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 创建文件
     *
     * @param path     文件路径
     * @param fileName 文件名
     * @return 文件File对象
     */
    public static File createFile(String path, String fileName) {
        File file = new File(makeDir(path), fileName);
        if (!isExist(file)) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 判断File对象所指的目录或文件是否存在
     *
     * @param file File对象
     * @return true表示存在 false反之
     */
    public static boolean isExist(File file) {
        return file.exists();
    }
}
