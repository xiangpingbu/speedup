package com.ecreditpal.maas.common;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lifeng
 * @version 1.0 on 2017/3/7.
 */
public class ExcelUtil {
    public static void read(String path) throws IOException {
        Gson gson = new Gson();
        InputStream is = new FileInputStream(path);
//        String type = path.substring(path.lastIndexOf(","), path.length());
        //for xlsx
        Workbook wb = new XSSFWorkbook(is);

//        List<String> list = Lists.newArrayList();
        LinkedHashMap<String,String> map = Maps.newLinkedHashMap();

        Sheet sheet1 = wb.getSheetAt(0);
        StringBuilder sb = new StringBuilder("");
        for (Row row : sheet1) {
            int i=0;
            sb = new StringBuilder();
            String head = "";
            for (Cell cell : row) {
                if (i==0){
                    head = cell.getStringCellValue();
                    map.put(head,"");
                }else {
                    map.put(head,cell.getStringCellValue());
                }
                i++;
            }
        }
        map.remove("字段");
        map.remove("");
        String json = gson.toJson(map);
        System.out.println(json);

        map = Maps.newLinkedHashMap();
        Sheet sheet2 = wb.getSheetAt(1);
        for (Row row : sheet2) {
            int i=0;
            sb = new StringBuilder();
            String head = "";
            for (Cell cell : row) {
                if (i==0){
                    head = cell.getStringCellValue();
                    map.put(head,"");
                }else {
                    map.put(head,cell.getStringCellValue());
                }
                i++;
            }
        }
        map.remove("我方variable");
         json = gson.toJson(map);
        System.out.println(json);

        map = Maps.newLinkedHashMap();
        Sheet sheet3 = wb.getSheetAt(2);
        for (Row row : sheet3) {
            int i=0;
            sb = new StringBuilder();
            String head = "";
            for (Cell cell : row) {
                if (i==0){
                    head = cell.getStringCellValue();
                    map.put(head,"");
                }else {
                    map.put(head,cell.getStringCellValue());
                }
                i++;
            }
        }
        map.remove("我方变量");
        json = gson.toJson(map);
        System.out.println(json);

    }

    public static void main(String[] args) throws IOException {
        String path = "/Users/lifeng/Downloads/百融API人类语言映射.xlsx";
        read(path);
    }
}
