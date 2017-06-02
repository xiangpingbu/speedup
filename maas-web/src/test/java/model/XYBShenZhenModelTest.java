package model;

import com.ecreditpal.maas.common.excel.ExcelContent;
import com.ecreditpal.maas.common.excel.ExcelReaderUtil;
import com.ecreditpal.maas.common.excel.ExcelRowReader;
import com.ecreditpal.maas.common.utils.file.ConfigurationManager;
import com.ecreditpal.maas.common.utils.http.MyHttpClient;
import com.ecreditpal.maas.model.model.scorecard.AKDModel;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.HashMap;
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
        ExcelReaderUtil.readExcel(rowReader, ConfigurationManager.getConfiguration().getString("xyb_ShenZhen_testing.xlsx"));
        ExcelContent content = rowReader.getRows();

        Map<String, Integer> head = content.getHead();
        AKDModel model = new AKDModel();
        double delta = 0.000001d;
        int index = 2;
        for (List<String> list : content.getContent()) {


            Map<String, Object> map = Maps.newHashMap();
            map.put("degree",list.get(head.get("")));
            map.put("gender",list.get(head.get("")));
            map.put("age",list.get(head.get("")));
            map.put("cellPhoneAccessTime",list.get(head.get("")));
            map.put("zhiMaCredit",list.get(head.get("")));
            map.put("liveCase",list.get(head.get("")));
            map.put("pIdCitySameInd",list.get(head.get("")));
            map.put("companyType",list.get(head.get("")));
            map.put("marriageStatus",list.get(head.get("")));


            BigDecimal a = new BigDecimal((Double) model.run(map)).setScale(8, BigDecimal.ROUND_HALF_UP);
            BigDecimal b = new BigDecimal(list.get(head.get("aligned_score"))).setScale(8, BigDecimal.ROUND_HALF_UP);

            if (Math.abs(a.subtract(b).doubleValue()) > delta) {
                System.out.println(index + "行匹配不成功,a:" + a.doubleValue() + "b:" + b.doubleValue());
            }
            index++;
        }
    }
}
