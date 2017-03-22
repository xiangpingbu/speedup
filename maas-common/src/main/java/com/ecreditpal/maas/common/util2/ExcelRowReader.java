package com.ecreditpal.maas.common.util2;

import com.ecreditpal.maas.common.utils.ExcelContent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lifeng
 * @version 1.0 on 2017/3/17.
 */
public class ExcelRowReader implements IExcelRowReader {

    private static LinkedHashMap<String, Integer> head = Maps.newLinkedHashMap();
    private static List<List<String>> excelRow = Lists.newArrayList();
    private static ExcelContent excelContent;
    private static int i = 0;
    private static int a = 0;


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
        excelRow.add(content);
        i++;
    }

    public ExcelContent getRows() {
        excelRow.remove(0);
        ExcelContent excelContent = new ExcelContent();
        excelContent.setContent(excelRow);
        excelContent.setHead(head);
        return excelContent;
    }

}