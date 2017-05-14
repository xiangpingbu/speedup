package model;

import com.ecreditpal.maas.common.excel.ExcelContent;
import com.ecreditpal.maas.common.excel.ExcelReaderUtil;
import com.ecreditpal.maas.common.excel.ExcelRowReader;
import com.ecreditpal.maas.common.utils.file.ConfigurationManager;
import com.ecreditpal.maas.model.model.AKDModel;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author lifeng
 * @CreateTime 2017/5/11.
 */
public class AkdModelTest {

    @Test
    public void excelHelp() throws Exception {

        ExcelRowReader rowReader = new ExcelRowReader();
        ExcelReaderUtil.readExcel(rowReader, ConfigurationManager.getConfiguration().getString("akd_testing.xlsx"));
        ExcelContent content = rowReader.getRows();

        Map<String, Integer> head = content.getHead();
        AKDModel model = new AKDModel();
        double delta = 0.000001d;
//        List<Integer> scoreList = Lists.newArrayListWithCapacity(50000);
        int index = 2;
        for (List<String> list : content.getContent()) {
            Map<String, Object> map = Maps.newHashMap();

//            map.put("creditQueryTimes", list.get(head.get("credit_query_times_three")));  //信用卡过去三个月查询次数

//                List<Double> list = Lists.newArrayList(1000.0, 5100.0);
            map.put("cellPhoneAccessTime", list.get(head.get("手机入网时间")));
            map.put("degree", list.get(head.get("学历"))); //已经使用的总额度
            map.put("netFlow", list.get(head.get("net_flow")));//信用总额度
            map.put("gender", list.get(head.get("性别")));//个人学历,2为本科
            map.put("companyType", list.get(head.get("公司性质")));//居住情况,预期的值中并不存在9
            map.put("age", list.get(head.get("年龄")));//客户的性别 1为男
            map.put("marriageStatus", list.get(head.get("婚姻状况"))); //共同居住者,4为其他
            map.put("contactsClass1Cnt", list.get(head.get("contacts_class1_cnt")));//年收入


            BigDecimal a = new BigDecimal((Double) model.run(map)).setScale(8,BigDecimal.ROUND_HALF_UP);
            BigDecimal b = new BigDecimal(list.get(head.get("aligned_score"))).setScale(8,BigDecimal.ROUND_HALF_UP);

            if (Math.abs(a.subtract(b).doubleValue()) > delta ){
                System.out.println(index+"行匹配不成功,a:"+a.doubleValue()+"b:"+b.doubleValue());
            }
            index++;
        }
    }
}
