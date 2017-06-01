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
public class AkdModelTest {


    /**
     * 测试  模型根据参数得到的结果和excel得到的结果是否一致
     *
     * @throws Exception 读取文件时抛出异常
     */
    public void excelHelp() throws Exception {

        ExcelRowReader rowReader = new ExcelRowReader();
        ExcelReaderUtil.readExcel(rowReader, ConfigurationManager.getConfiguration().getString("akd_testing.xlsx"));
        ExcelContent content = rowReader.getRows();

        Map<String, Integer> head = content.getHead();
        AKDModel model = new AKDModel();
        double delta = 0.000001d;
        int index = 2;
        for (List<String> list : content.getContent()) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("cellPhoneAccessTime", list.get(head.get("手机入网时间")));
            map.put("degree", list.get(head.get("学历"))); //已经使用的总额度
            map.put("netFlow", list.get(head.get("net_flow")));//信用总额度
            map.put("gender", list.get(head.get("性别")));//个人学历,2为本科
            map.put("companyType", list.get(head.get("公司性质")));//居住情况,预期的值中并不存在9
            map.put("age", list.get(head.get("年龄")));//客户的性别 1为男
            map.put("marriageStatus", list.get(head.get("婚姻状况"))); //共同居住者,4为其他
            map.put("contactsClass1Cnt", list.get(head.get("contacts_class1_cnt")));//年收入


            BigDecimal a = new BigDecimal((Double) model.run(map)).setScale(8, BigDecimal.ROUND_HALF_UP);
            BigDecimal b = new BigDecimal(list.get(head.get("aligned_score"))).setScale(8, BigDecimal.ROUND_HALF_UP);

            if (Math.abs(a.subtract(b).doubleValue()) > delta) {
                System.out.println(index + "行匹配不成功,a:" + a.doubleValue() + "b:" + b.doubleValue());
            }
            index++;
        }
    }

    /**
     * 测试两个http请求返回的结果是否一致
     */
    @Test
    public void httpRequestSourceTest() throws IOException, URISyntaxException {
        MyHttpClient client = MyHttpClient.getInstance();
//        ApacheHttpClient client1 = ApacheHttpClient.getInstance();
        Map<String, String> map = new HashMap<>();
        map.put("degree", "本科");
        map.put("cellPhoneAccessTime", "五年以上");
        map.put("gender", "男");
        map.put("companyType", "机关及事业单位");
        map.put("age", "25");
        map.put("marriageStatus", "再婚");
        map.put("netFlow", "500");
        map.put("contactsClass1Cnt", "40");
        map.put("account", "lifeng");
        map.put("password", "MTIz");
        map.put("apiCode", "M112");


        String remoteHost = client.post("http://121.196.192.146:9080/rest/api/M112", map);
        log.debug("remoteHost result:{}", remoteHost);
        String ecreditpal = client.post("https://api.ecreditpal.cn/ecreditpal/rest/api/v2", map);
        log.debug("ecreditpal result:{}", ecreditpal);

        JsonParser jsonParser = new JsonParser();
        JsonObject result = jsonParser.parse(ecreditpal).getAsJsonObject()
                .getAsJsonObject("data")
                .getAsJsonObject("MAAS.M112")
                .getAsJsonObject("result");

        JsonObject remoteResult = jsonParser.parse(remoteHost).getAsJsonObject();

        Assert.assertEquals("结果匹配不一致",remoteResult.get("success"),result.get("success"));
        Assert.assertEquals("结果匹配不一致",remoteResult.get("data"),result.get("data"));


//        String baidu = client.get("https://www.baidu.com");
//        log.debug("ecreditpal result:{}",baidu);

    }
}
