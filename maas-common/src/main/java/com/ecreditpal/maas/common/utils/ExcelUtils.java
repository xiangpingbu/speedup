package com.ecreditpal.maas.common.utils;

import com.ecreditpal.maas.common.offline.XybJsonConvert;
import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * @author lifeng
 * @CreateTime 2017/4/18.
 */
public class ExcelUtils {

    public  static void exportFile(String filePath, List<String> head,List<List<String>> content) {
        Workbook wb = new SXSSFWorkbook(51000); // keep 100 rows in memory, exceeding rows will be flushed to disk
        Sheet sh = wb.createSheet();
        Row row = sh.createRow(0);
        int cellNum = 0;
        for (String s : head) {
            Cell cell = row.createCell(cellNum);
            cell.setCellValue(s);
            cellNum++;
        }


        for (int rownum = 0; rownum < content.size(); rownum++) {
            row = sh.createRow(rownum+1);
            List<String> rowContent = content.get(rownum);
            for (int cellnum = 0; cellnum < rowContent.size(); cellnum++) {
                Cell cell = row.createCell(cellnum);
//                String address = new CellReference(cell).formatAsString();
                cell.setCellValue(rowContent.get(cellnum));
            }
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);
            wb.write(out);
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        List<String> head = Lists.newArrayList("real_name", "id_card_num", "gender", "age", "province", "city", "region", "state",
                "reliability", "cid_auth",
                "friendster", "credit_apply", "call_aomen", "call_lawyer", "call_court");
        List<List<String>> content = XybJsonConvert.transfer4_0();
        exportFile("/Users/lifeng/Desktop/data.xlsx",head,content);


//        List<String> head = Lists.newArrayList("phone_gray_score","contacts_class1_blacklist_cnt","contacts_class2_blacklist_cnt","contacts_class1_cnt","contacts_router_cnt","contacts_router_ratio",
//                "user_name","id_card_num","gender","age","province","city","region",
//                "reliability","friendster","phone_used_time","phone_silent","contact_each_other","contact_macao","contact_lawyer"
//                ,"contact_court","contact_night","contact_loan","contact_bank","contact_credit_card","cell_operator","cell_operator_zh",
//                "cell_phone_num","cell_loc","call_cnt","call_out_cnt","call_out_time","call_in_cnt","call_in_time","net_flow","sms_cnt","total_amount");
//        List<List<String>> content = XybJsonConvert.transfer4_2();
//        exportFile("/Users/lifeng/Desktop/data.xlsx",head,content);
    }
}
