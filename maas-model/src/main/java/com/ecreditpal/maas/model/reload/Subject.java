package com.ecreditpal.maas.model.reload;

/**
 * 任务的注册和读取接口
 * @author lifeng
 * @version 1.0 on 2017/3/6.
 */
public interface Subject {
    public void register(Register m);

    public void reload();

}
