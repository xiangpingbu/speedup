package com.ecreditpal.maas.model.bean;

import lombok.Getter;
import lombok.Setter;
import org.javalite.activejdbc.Model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author lifeng
 * @version 1.0 on 2017/3/10.
 */
@Setter
@Getter
@XmlRootElement
public class Data{
    private Integer number;
}
