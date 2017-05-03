package com.ecreditpal.maas.model.reload;

/**
 * @author lifeng
 * @version 1.0 on 2017/3/6.
 */
public interface Register {
    /**
     *
     * 拥有Register集合的类将会调用work方法
     * 通过该方法通知实现该接口的类.
     */
    public void work();

}
