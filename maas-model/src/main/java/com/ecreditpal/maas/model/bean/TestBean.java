package com.ecreditpal.maas.model.bean;

import com.ecreditpal.maas.common.db.activejdbc.MakeInstrumentationUtil;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

import java.util.List;

/**
 * @author lifeng
 * @version 1.0 on 2017/5/1.
 */
@Table("tool_model")
@IdName("id")
public class TestBean  extends Model{

    public static void main(String[] args) {
        MakeInstrumentationUtil.make();
        List<TestBean> t =    TestBean.where("id=2");
    }
}
