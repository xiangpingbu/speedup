package com.ecreditpal.maas.model.handler;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author lifeng
 * @CreateTime 2017/4/14.
 */

@Getter
@Setter
public abstract class  RequestParam {
     Map<String, Object> param;
}
