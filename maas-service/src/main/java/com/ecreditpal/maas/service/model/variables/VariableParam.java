package com.ecreditpal.maas.service.model.variables;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author lifeng
 * @version 1.0 on 2017/3/7.
 */
@XmlRootElement(name = "param")
public class VariableParam {
    private String paramType;
    private String paramName;
    private String paramValue;
    private String mapping;


    @XmlAttribute(name = "value", required = false)
    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }


    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    @XmlAttribute(name = "name", required = false)
    public String getParamName() {
        return paramName;
    }


    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    @XmlAttribute(name = "type", required = false)
    public String getParamType() {
        return paramType;
    }
    @XmlAttribute(name = "mapping", required = false)
    public String getMapping() {
        return mapping;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }
}
