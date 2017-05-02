package com.ecreditpal.maas.model.bean.db;

import com.ecreditpal.maas.common.db.activejdbc.ActiveJDBCUtil;
import com.ecreditpal.maas.common.db.activejdbc.MakeInstrumentationUtil;

import java.util.List;

/**
 * @author lifeng
 * @version 1.0 on 2017/5/1.
 */
public class DBUsageDemo {
    public static void main(String[] args) {
        MakeInstrumentationUtil.make();

        //初始化数据库连接
        ActiveJDBCUtil.getDB("tool");
        //单表sql查询
        List<ToolModel> t = ToolModel.where("model_name = ?","model_train_selected");
        for (ToolModel toolModel : t) {
            System.out.println(toolModel.getIsDeleted());
            System.out.println(toolModel.getModelTarget());
            System.out.println(toolModel.getModelBranch());
        }
    }
}
