package com.ecreditpal.maas.common.excel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author lifeng
 * @version 1.0 on 2017/3/17.
 */
public class ExcelRowReader implements IExcelRowReader {

    private  LinkedHashMap<String, Integer> head = Maps.newLinkedHashMap();
    private  List<List<String>> excelRow = Lists.newArrayList();
    private  ExcelContent excelContent ;
    private  int i = 0;
    private  int a = 0;


    @Override
    public void getRows(int sheetIndex, int curRow, List<String> rowlist) {
//        Map<String, String> content = Maps.newLinkedHashMap();
        List<String> content = Lists.newArrayList();
        int  b=0;
        for (String s : rowlist) {
            if (i == 0) {
                head.put(s,a);
            } else {
                if (b!=0) {
                    content.add(s);
                }
                b++;
            }
            a++;
        }
        if (i==0){
            i++;
            return;
        }
        excelRow.add(content);
        i++;
    }

    public ExcelContent getRows() {
        excelContent = new ExcelContent();
        excelContent.setContent(excelRow);
        excelContent.setHead(head);
        return excelContent;
    }

}