package com.ecreditpal.maas.common.file;

import java.io.FileNotFoundException;
import java.net.URL;

/**
 * @author lifeng
 * @version 1.0 on 2017/3/17.
 */
public class FileUtil {
   private static ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    public static String getFilePath(String fileName) {
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            try {
                throw new FileNotFoundException("can not find file:"+fileName);
            } catch (FileNotFoundException e) {
                return null;
            }
        }
        return resource.getPath();
    }
}
