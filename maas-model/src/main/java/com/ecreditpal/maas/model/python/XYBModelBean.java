package com.ecreditpal.maas.model.python;

import java.util.List;

/**
 * @author lifeng
 * @version 1.0 on 2017/3/8.
 */
public class XYBModelBean {
    /**
     * 三个月内的查询次数
     */
    private String creditQueryTimes;
    /**
     * 信用卡额度列表
     */
    private List<Double> creditLimitList;
    /**
     * 信用卡使用额度
     */
    private String totalUsedLimit;
    /**
     * 信用卡总额度
     */
    private String totalCreditLimit;
    /**
     * 最高学历
     */
    private String personalEducation;
    /**
     * 住房情况
     */
    private String personalLiveCase;
    /**
     * 性别
     */
    private String clientGender;
    /**
     * 居住情况
     */
    private String personalLiveJoin;
    /**
     * 年收入
     */
    private String personalYearIncome;
    /**
     * 贷款偿还频率列表
     */
    private List<Double> repaymentFrequencyList;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 贷款申请日期
     */
    private String nowadays;


    public String getCreditQueryTimes() {
        return creditQueryTimes;
    }

    public void setCreditQueryTimes(String creditQueryTimes) {
        this.creditQueryTimes = creditQueryTimes;
    }

    public List<Double> getCreditLimitList() {
        return creditLimitList;
    }

    public void setCreditLimitList(List<Double> creditLimitList) {
        this.creditLimitList = creditLimitList;
    }

    public String getTotalUsedLimit() {
        return totalUsedLimit;
    }

    public void setTotalUsedLimit(String totalUsedLimit) {
        this.totalUsedLimit = totalUsedLimit;
    }

    public String getTotalCreditLimit() {
        return totalCreditLimit;
    }

    public void setTotalCreditLimit(String totalCreditLimit) {
        this.totalCreditLimit = totalCreditLimit;
    }

    public String getPersonalEducation() {
        return personalEducation;
    }

    public void setPersonalEducation(String personalEducation) {
        this.personalEducation = personalEducation;
    }

    public String getPersonalLiveCase() {
        return personalLiveCase;
    }

    public void setPersonalLiveCase(String personalLiveCase) {
        this.personalLiveCase = personalLiveCase;
    }

    public String getClientGender() {
        return clientGender;
    }

    public void setClientGender(String clientGender) {
        this.clientGender = clientGender;
    }

    public String getPersonalLiveJoin() {
        return personalLiveJoin;
    }

    public void setPersonalLiveJoin(String personalLiveJoin) {
        this.personalLiveJoin = personalLiveJoin;
    }

    public String getPersonalYearIncome() {
        return personalYearIncome;
    }

    public void setPersonalYearIncome(String personalYearIncome) {
        this.personalYearIncome = personalYearIncome;
    }

    public List<Double> getRepaymentFrequencyList() {
        return repaymentFrequencyList;
    }

    public void setRepaymentFrequencyList(List<Double> repaymentFrequencyList) {
        this.repaymentFrequencyList = repaymentFrequencyList;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getNowadays() {
        return nowadays;
    }

    public void setNowadays(String nowadays) {
        this.nowadays = nowadays;
    }
}
