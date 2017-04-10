package com.ecreditpal.maas.common.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lifeng
 * @version 1.0 on 2017/3/7.
 */
public class ExcelUtil2 {
    public static ExcelContent read(String path) throws IOException {
        File file = new File(path);
//        InputStream is = new FileInputStream(path);
        Workbook wb = new XSSFWorkbook(new BufferedInputStream(new FileInputStream(file)));

        ExcelContent excel = new ExcelContent();
        LinkedHashMap<Integer, String> head = Maps.newLinkedHashMap();
//        List<String> content ;
        Map<String,String> content ;
        List<Map<String,String>> excelRow = Lists.newArrayList();

        Sheet sheet1 = wb.getSheetAt(0);
        int i = 0;
        for (Row row : sheet1) {
            int a = 0;
            int b = 0;
            content = Maps.newLinkedHashMap();
            for (Cell cell : row) {
                if (i == 0) {
                    head.put(a,cell.getStringCellValue());
                } else {
                    content.put(head.get(b),cell.getStringCellValue());
                    b++;
                }
                a++;
                System.out.println(a);
            }
            excelRow.add(content);
            i++;
        }
//        excel.setContent(excelRow);

        return excel;

    }

    public void write() {
        String path = "/Users/lifeng/Downloads/百融API人类语言映射的副本.xlsx";

    }

    public static void main(String[] args) throws IOException {
        String path = "/Users/lifeng/Desktop/all_data_selected.xlsx";
        ExcelContent content = read(path);
    }
}
