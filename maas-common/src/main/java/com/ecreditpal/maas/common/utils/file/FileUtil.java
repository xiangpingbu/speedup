package com.ecreditpal.maas.common.utils.file;
import java.io.File;
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

    public static String getRootPath() {

        String rootPath = new File(System.getProperty("user.dir")).getParent();
        //判断/maas/maas,因为从git上pull下来时会自动创建maas目录
        //如果不熟悉,可能会提早创建maas目录
        if (!(rootPath.endsWith("/maas/maas") ||rootPath.endsWith("/maas"))) {
            rootPath = rootPath +"/maas";
        }
        return rootPath;
    }
}
