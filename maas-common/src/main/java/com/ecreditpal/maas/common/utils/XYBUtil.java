package com.ecreditpal.maas.common.utils;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;

/**
 * @author lifeng
 * @version 1.0 on 2017/3/17.
 */
public class XYBUtil {

    public static void main(String[] args) throws IOException, InvalidFormatException {
        String path = "/Users/lifeng/Downloads/百融API人类语言映射.xlsx";
        ExcelContent content = ExcelUtil2.read(path);
    }
}
