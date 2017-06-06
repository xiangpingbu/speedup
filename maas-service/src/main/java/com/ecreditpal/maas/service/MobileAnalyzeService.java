package com.ecreditpal.maas.service;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.javalite.activejdbc.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author lifeng
 * @version 1.0 on 2017/6/5.
 */
@Slf4j
public class MobileAnalyzeService {

    private static MobileAnalyzeService instance = new MobileAnalyzeService();

    private static final int MOBILE_DIGITS_COUNT = 11;

    private static final Set<String> MOBILE_PHONE_PREFIX_SET = new HashSet<>(Arrays.asList("130", "131", "132", "133",
            "134", "135", "136", "137", "138", "139", "140", "141", "142", "143", "144", "145", "146", "147", "148",
            "149", "150", "151", "152", "153", "154", "155", "156", "157", "158", "159", "170", "171", "173", "175",
            "175", "176", "177", "178", "180", "181", "182", "183", "184", "185", "186", "187", "188", "189"));

    private static final Map<String, String> CARRIER_MAP = ImmutableMap.<String, String>builder()
            .put("133", "电信")
            .put("153", "电信")
            .put("180", "电信")
            .put("181", "电信")
            .put("189", "电信")
            .put("177", "电信")
            .put("173", "电信")
            .put("149", "电信")
            .put("130", "联通")
            .put("131", "联通")
            .put("132", "联通")
            .put("145", "联通")
            .put("155", "联通")
            .put("156", "联通")
            .put("176", "联通")
            .put("185", "联通")
            .put("186", "联通")
            .put("134", "移动")
            .put("135", "移动")
            .put("136", "移动")
            .put("137", "移动")
            .put("138", "移动")
            .put("139", "移动")
            .put("147", "移动")
            .put("150", "移动")
            .put("151", "移动")
            .put("152", "移动")
            .put("157", "移动")
            .put("158", "移动")
            .put("159", "移动")
            .put("178", "移动")
            .put("182", "移动")
            .put("183", "移动")
            .put("184", "移动")
            .put("187", "移动")
            .put("188", "移动")
            .build();


    private MobileAnalyzeService() {
    }

    public static MobileAnalyzeService getInstance() {
        return instance;
    }

    public boolean isValidMobileNumber(String mobileNumber) {

        // must be all digits
        if (!mobileNumber.matches("\\d+")) {
            return false;
        }

        // must be 11 digits long
        if (mobileNumber.length() != MOBILE_DIGITS_COUNT) {
            return false;
        }

        // must contain a valid prefix
        if (!MOBILE_PHONE_PREFIX_SET.contains(mobileNumber.substring(0, 3))) {
            return false;
        }

        return true;
    }



    public static void main(String[] args) {
        System.out.println(MobileAnalyzeService.getInstance().isValidMobileNumber("13738121017"));
    }
}
