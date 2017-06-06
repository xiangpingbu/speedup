package com.ecreditpal.maas.model.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lifeng
 * @version 1.0 on 2017/6/5.
 */
@Getter
@Setter
public class IdCardInfoBean {
    public Integer year;
    public Integer month;
    public Integer day;
    public String gender;
    public String region;
    public Integer age;
}
