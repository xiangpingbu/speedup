package model;

import com.ecreditpal.maas.common.excel.ExcelReaderUtil;
import com.ecreditpal.maas.common.excel.ExcelRowReader;
import com.ecreditpal.maas.common.excel.ExcelContent;
import com.ecreditpal.maas.service.model.scorecard.XYBModel;
import com.ecreditpal.maas.service.model.variables.Variable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;


/**
 * @author lifeng
 * @version 1.0 on 2017/3/2.
 */
public class XYBModelTest {
    /**
     * 信用宝模型单元测试
     *
     * @throws Exception
     */

    @Test
    public void testModel() throws Exception {
        XYBModel model = new XYBModel();
        Map<String, Object> map = Maps.newHashMap();
        map.put("creditLimit", 1);  //信用卡过去三个月查询次数
        map.put("creditQueryTimes", 1);
        System.out.println(System.getProperty("user.dir"));

        map.put("creditUtilization", 0); //已经使用的总额度
        map.put("personalEducation", 1);//个人学历,2为本科
        map.put("personalLiveCase", "5");//居住情况,预期的值中并不存在9
        map.put("clientGender", "1");//客户的性别 1为男
        map.put("personalLiveJoin", "1,"); //共同居住者,1为父母
        map.put("personalYearIncome", 50);//年收入

        map.put("age", 32);//客户的年龄

        int modelTestScore = 624;
        List<Variable> variableList = model.getVariableList();
//        model.run(map);
//        model.run(map);
        Assert.assertEquals("模型结果不符合预期",modelTestScore, model.run(map));

        Assert.assertEquals("信用卡过去三个月查询次数不符合预期", "1", variableList.get(0).getValue());
        Assert.assertEquals("信用卡的总额度不符合预期", "1", variableList.get(1).getValue());
        Assert.assertEquals("信用卡的使用率不符合预期", "0", variableList.get(2).getValue());
        Assert.assertEquals("用户的学历不符合预期", "masterOrAbove", variableList.get(3).getValue());
        Assert.assertEquals("用户的居住情况不符合预期", "rentalRoom", variableList.get(4).getValue());
        Assert.assertEquals("用户的性别不符合预期", "male", variableList.get(5).getValue());
        Assert.assertEquals("共同居住者不符合预期", "parents", variableList.get(6).getValue());
        Assert.assertEquals("年收入不符合预期", "50", variableList.get(7).getValue());
        Assert.assertEquals("平均贷款的还款频率不符合预期", "missing", variableList.get(8).getValue());
        Assert.assertEquals("年龄不符合预期", "32", variableList.get(9).getValue());
    }


    public void excelHelp() throws Exception {

        ExcelRowReader rowReader = new ExcelRowReader();
        ExcelReaderUtil.readExcel(rowReader, "/Users/lifeng/Desktop/wo_pai/all_data_selected.xlsx");
        ExcelContent content = rowReader.getRows();

        Map<String, Integer> head = content.getHead();
        XYBModel model = new XYBModel();
        List<Integer> scoreList = Lists.newArrayListWithCapacity(50000);
        for (List<String> list : content.getContent()) {
            Map<String, Object> map = Maps.newHashMap();

            map.put("creditQueryTimes", list.get(head.get("credit_query_times_three")));  //信用卡过去三个月查询次数

//                List<Double> list = Lists.newArrayList(1000.0, 5100.0);
            map.put("creditLimit", list.get(head.get("credit_c_credit_amount_sum")));//信用卡的总额度
            map.put("totalUsedLimit", list.get(head.get("credit_total_used_limit"))); //已经使用的总额度
            map.put("totalCreditLimit", list.get(head.get("credit_total_credit_limit")));//信用总额度
            map.put("personalEducation", list.get(head.get("personal_education")));//个人学历,2为本科
            map.put("personalLiveCase", list.get(head.get("personal_live_case")));//居住情况,预期的值中并不存在9
            map.put("clientGender", list.get(head.get("client_gender")));//客户的性别 1为男
            map.put("personalLiveJoin", list.get(head.get("personal_live_join"))); //共同居住者,4为其他
            map.put("personalYearIncome", list.get(head.get("personal_year_income")));//年收入

//                List<Double> list2 = Lists.newArrayList(2.0, 3.0);
            map.put("repaymentFrequency", list.get(head.get("loan_repayment_frequency_avg")));//平均贷款的还款频率,前两个是不合格数据
            map.put("birthday", list.get(head.get("client_birthday")));//客户的生日
            map.put("loanDay", list.get(head.get("app_income_time")));//申请时间
            Integer score = (Integer) model.run(map);
            list.add(score.toString());
        }

        head.put("score", 20);


        Workbook wb = new SXSSFWorkbook(51000); // keep 100 rows in memory, exceeding rows will be flushed to disk
        Sheet sh = wb.createSheet();
        Row row = sh.createRow(0);
        int cellNum = 0;
        for (String s : head.keySet()) {
            Cell cell = row.createCell(cellNum);
            cell.setCellValue(s);
            cellNum++;
        }


        for (int rownum = 0; rownum < content.getContent().size(); rownum++) {
             row = sh.createRow(rownum+1);
             List<String> rowList = content.getContent().get(rownum);
            for (int cellnum = 0; cellnum < rowList.size(); cellnum++) {
                Cell cell = row.createCell(cellnum);
//                String address = new CellReference(cell).formatAsString();
                cell.setCellValue(rowList.get(cellnum));
            }
        }
        FileOutputStream out = new FileOutputStream("/Users/lifeng/Desktop/all_data_selected3.xlsx");
        wb.write(out);
        out.close();

    }

    public static void main(String[] args) {

    }

}