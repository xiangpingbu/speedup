package model;

import com.ecreditpal.maas.common.excel.ExcelContent;
import com.ecreditpal.maas.common.excel.ExcelReaderUtil;
import com.ecreditpal.maas.common.excel.ExcelRowReader;
import com.ecreditpal.maas.common.utils.file.ConfigurationManager;
import com.ecreditpal.maas.service.model.scorecard.AKDModel;
import com.ecreditpal.maas.service.model.scorecard.XYBShenZhenModel;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author lifeng
 * @CreateTime 2017/5/11.
 */
@Slf4j
public class XYBShenZhenModelTest {


    /**
     * 测试  模型根据参数得到的结果和excel得到的结果是否一致
     *
     * @throws Exception 读取文件时抛出异常
     */
    public void excelHelp() throws Exception {

        ExcelRowReader rowReader = new ExcelRowReader();
        ExcelReaderUtil.readExcel(rowReader, ConfigurationManager.getConfiguration().getString("szxyb_testing.xlsx"));
        ExcelContent content = rowReader.getRows();

        Map<String, Integer> head = content.getHead();
        XYBShenZhenModel model = new XYBShenZhenModel();
        double delta = 0.000001d;
        int index = 1;
        for (List<String> list : content.getContent()) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("degree",list.get(head.get("学历")));
            map.put("gender",list.get(head.get("性别")));
            map.put("age",list.get(head.get("age")));
            map.put("cellPhoneAccessTime",list.get(head.get("手机入网时间")));
            map.put("zhiMaCredit",list.get(head.get("芝麻信用")));
            map.put("liveCase",list.get(head.get("居住情况")));
            map.put("pIdCitySameInd",list.get(head.get("p_id_city_same_ind")));
            map.put("companyType",list.get(head.get("公司性质")));
            map.put("marriageStatus",list.get(head.get("婚姻状况")));
            map.put("phoneGrayScore",list.get(head.get("phone_gray_score")));


            BigDecimal a = new BigDecimal((Integer) model.run(map));
            BigDecimal b = new BigDecimal((int)model.scoreAlign(model.scoreToLogit(Double.valueOf(list.get(head.get("prob_bad_w_gray"))))));
            if (Math.abs(a.subtract(b).doubleValue()) > delta) {
                System.out.println(index + "行匹配不成功,a:" + a.doubleValue() + "b:" + b.doubleValue());
            }
            index++;
        }
    }
}
