package com.ecreditpal.maas.service.model.variables;

import java.util.List;

/**
 * Created by xibu on 9/28/16.
 */
public class VariableConfiguration {
    private List<Variable> variables;

    private String Model;

    public String getModel() {
        return Model;
    }

    public void setModel(String Model) {
        this.Model = Model;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }


}
