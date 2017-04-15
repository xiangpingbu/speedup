package com.ecreditpal.maas.common.utils.file;
import java.net.URL;

/**
 * @author lifeng
 * @version 1.0 on 2017/3/17.
 */
public class FileUtil {
    public static String getFilePath(String fileName) {
        ClassLoader classLoader = getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new RuntimeException("can not find file:" + fileName);
        }
        return resource.getPath();
    }

    private static ClassLoader getClassLoader() {
        ClassLoader cl ;
        cl = Thread.currentThread().getClass().getClassLoader();
        if (cl == null) {
                cl = ClassLoader.getSystemClassLoader();
        }
        return cl;
    }
}