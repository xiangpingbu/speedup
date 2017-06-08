package com.ecreditpal.maas.model.bean.model.miguan;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lifeng
 * @version 1.0 on 2017/6/2.
 */
@Getter
@Setter
public class MiguanVariableBean {
    @JSONField(name="gray_profile")
    private GrayProfile grayProfile;
}
