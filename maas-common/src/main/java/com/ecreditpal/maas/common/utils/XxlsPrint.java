package com.ecreditpal.maas.common.utils;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lifeng
 * @version 1.0 on 2017/3/17.
 */
public class XxlsPrint extends XxlsAbstract {
    private LinkedHashMap<Integer, String> head = Maps.newLinkedHashMap();
    public  List<Map<String,String>> excelRow = Lists.newArrayList();
    private ExcelContent excelContent;
    private int i = 0;
    int a = 0;

   public XxlsPrint(ExcelContent excelContent) {
        this.excelContent = excelContent;
    }

    @Override
    public void optRows(int sheetIndex,int curRow, List<String> rowlist) throws SQLException {
//        Map<String, String> content = Maps.newLinkedHashMap();
//        int  b=0;
//        for (String s : rowlist) {
//            if (i == 0) {
//                head.put(a,s);
//            } else {
//                if (b!=0) {
//                    content.put(head.get(b), s);
//                }
//                b++;
//            }
//            a++;
//        }
//        excelRow.add(content);
//        i++;


    }

    public static void main(String[] args) throws Exception {
       ExcelContent excelContent = new ExcelContent();
        XxlsPrint howto = new XxlsPrint(excelContent);
        howto.processOneSheet("/Users/lifeng/Desktop/all_data_selected.xlsx",1);
//        howto.processOneSheet("/Users/lifeng/Downloads/百融API人类语言映射的副本.xlsx",1);
//      howto.processAllSheets("F:/new.xlsx");
//        excelContent.setContent(howto.excelRow);
        System.out.println(excelContent.getContent().size());
    }
}