package com.ecreditpal.maas.model.bean;

import com.ecreditpal.maas.common.db.activejdbc.MakeInstrumentationUtil;
import org.javalite.activejdbc.Base;

import java.util.List;

/**
 * @author lifeng
 * @version 1.0 on 2017/5/1.
 */
public class Test {
    public static void main(String[] args) {
        MakeInstrumentationUtil.make();
        Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/wool",
                "root", "Cisco123");
        List<TestBean> t = TestBean.where("id=22");
    }
}
