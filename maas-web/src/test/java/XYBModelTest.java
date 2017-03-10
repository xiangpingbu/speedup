import com.ecreditpal.maas.model.model.XYBModel;
import com.ecreditpal.maas.model.variable.Variable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;


/**
 * @author lifeng
 * @version 1.0 on 2017/3/2.
 */
public class XYBModelTest {
    /**
     * 信用宝模型单元测试
     * @throws Exception
     */

    @Test
    public void testModel2() throws Exception {
        XYBModel model = new XYBModel();
        Map<String, Object> map = Maps.newHashMap();
        map.put("creditQueryTimes", 1);  //信用卡过去三个月查询次数

        List<Double> list = Lists.newArrayList(1000.0, 5100.0);
        map.put("creditLimitList", list);//信用卡的总额度

        map.put("totalUsedLimit",0); //已经使用的总额度
        map.put("totalCreditLimit",10000);//信用卡的总额度
        map.put("personalEducation","硕士及以上");//个人学历,2为本科
        map.put("personalLiveCase","租房");//居住情况,预期的值中并不存在9
        map.put("clientGender","男");//客户的性别 1为男
        map.put("personalLiveJoin","父母"); //共同居住者,4为其他
        map.put("personalYearIncome",50);//年收入

        List<Double> list2 = Lists.newArrayList(2.0,3.0);
        map.put("repaymentFrequencyList",list2);//平均贷款的还款频率,前两个是不合格数据
        map.put("birthday",null);//客户的生日
        map.put("loanDay","2016/6/13 16:19");//申请时间

        int modelTestScore = 609;
        List<Variable> variableList = model.getVariableList();

//        model.run(map);
        Assert.assertEquals("模型结果不符合预期",modelTestScore, model.run(map));

        Assert.assertEquals("信用卡过去三个月查询次数不符合预期","1",variableList.get(0).getValue());
        Assert.assertEquals("信用卡的总额度不符合预期","6100.0",variableList.get(1).getValue());
        Assert.assertEquals("信用卡的使用率不符合预期","0.0",variableList.get(2).getValue());
        Assert.assertEquals("用户的学历不符合预期","masterOrAbove",variableList.get(3).getValue());
        Assert.assertEquals("用户的居住情况不符合预期","rentalRoom",variableList.get(4).getValue());
        Assert.assertEquals("用户的性别不符合预期","male",variableList.get(5).getValue());
        Assert.assertEquals("共同居住者不符合预期","parents",variableList.get(6).getValue());
        Assert.assertEquals("年收入不符合预期","50",variableList.get(7).getValue());
        Assert.assertEquals("平均贷款的还款频率不符合预期","2.5",variableList.get(8).getValue());
        Assert.assertEquals("年龄不符合预期","missing",variableList.get(9).getValue());


    }

}