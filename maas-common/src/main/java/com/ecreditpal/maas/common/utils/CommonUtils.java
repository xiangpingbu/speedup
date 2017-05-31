package com.ecreditpal.maas.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lifeng
 * @version 1.0 on 2017/5/24.
 */
public class CommonUtils {
    public static boolean isNumber(String expression) {
        Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");
        Matcher matcher = pattern.matcher(expression);
        return matcher.find();
    }
}
