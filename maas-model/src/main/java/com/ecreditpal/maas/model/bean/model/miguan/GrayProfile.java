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
public class GrayProfile {
    @JSONField(name="contacts_class1_count")
    private Integer class1Count; //直接联系人
    @JSONField(name = "phone_gray_score")
    private Integer phoneGrayScore; //手机灰度值
}
