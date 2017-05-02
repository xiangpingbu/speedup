package com.ecreditpal.maas.model.bean;

import org.javalite.activejdbc.Model;
import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 这是一个通用的bean,包含了所有的数据库映射类的通用字段
 * id 主键
 * create_date 创建时间
 * modify_date 修改时间
 * is_deleted 是否已被删除,0代表未被删除,1代表已经被删除
 * @author lifeng
 * @version 2017/5/2.
 */
public class CommonBean extends Model {
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public void setId(Long id){
        setLong("id",id);
    }

    public Long getId() {
        return getLong("id");
    }

    public void setCreateDate(Date date){
        setDate("create_date",date);
    }

    public Date getCreateDate() {
        return getDate("create_date");
    }

    public void setModifyDate(Date date){
        setDate("modify_date",date);
    }

    public Date getModifyDate() {
        return getDate("modify_date");
    }

    public void setIsDeleted(Integer isDeleted) {
        setInteger("is_deleted",isDeleted);
    }

    public Integer getIsDeleted() {
        return getInteger("is_deleted");
    }
}
