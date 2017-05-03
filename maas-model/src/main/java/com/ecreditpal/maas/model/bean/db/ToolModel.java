package com.ecreditpal.maas.model.bean.db;

import com.ecreditpal.maas.model.bean.db.CommonBean;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author lifeng
 * @version 1.0 on 2017/5/1.
 */
@DbName("tool")
@Table("tool_model")
@IdName("id")
public class ToolModel extends CommonBean {


    public String getModelName() {
        return getString("model_name");
    }

    public void setModelName(String modelName) {
        setString("model_name",modelName);
    }

    public String getModelTarget() {
        return getString("model_target");
    }

    public void setModelTarget(String modelTarget) {
        setString("model_target",modelTarget);
    }

    public String getModelBranch() {
        return getString("model_branch");
    }

    public void setModelBranch(String modelBranch) {
        setString("model_branch",modelBranch);
    }

    public String getRemoveList() {
        return getString("remove_list");
    }

    public void setRemoveList(String removeList) {
         setString("remove_list",removeList);
    }

    public String getSelectedList() {
        return getString("selected_list");
    }

    public void setSelectedList(String removeList) {
        setString("selected_list",removeList);
    }

    public static void main(String[] args) {
        System.out.println(123);
    }
}
