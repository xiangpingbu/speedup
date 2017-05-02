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
@DbName("tool")
@Table("tool_model")
@IdName("id")
public class ToolModel extends CommonBean{


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


}
