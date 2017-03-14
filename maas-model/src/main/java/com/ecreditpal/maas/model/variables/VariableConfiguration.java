package com.ecreditpal.maas.model.variables;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by xibu on 9/28/16.
 */
@XmlRootElement(name = "VariableConfiguration")
public class VariableConfiguration {
    private List<Variable> variables;

    @XmlElement
    private String Model;

    public String getModel() {
        return Model;
    }

    public void setModel(String Model) {
        this.Model = Model;
    }

    @XmlElementWrapper(name = "Variables")
    @XmlElement(name = "Variable")
    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }


}
