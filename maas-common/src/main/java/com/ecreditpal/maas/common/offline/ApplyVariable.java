package com.ecreditpal.maas.common.offline;

import com.ecreditpal.maas.common.utils.CommonUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * @author lifeng
 * @version 1.0 on 2017/5/24.
 */
@Getter
@Setter
public class ApplyVariable {
    String woe;
    Boolean type;
    String minBoundary;
    String maxBoundary;
    Set<String> categoricalValue;



    public String getWoe(String value) {
        double min;
        double max;
        if (type) {
            if (!CommonUtils.isNumber(value))
                return "0.0";
            if ("nan".equals(value) && "nan".equals(minBoundary)) {
                return woe;
            }

            if ("-inf".equals(minBoundary)) {
                min = (double) Long.MIN_VALUE;
            } else {
                min = Double.valueOf(minBoundary);
            }

            if ("inf".equals(maxBoundary)) {
                max = (double) Long.MAX_VALUE;
            } else {
                max = Double.valueOf(maxBoundary);
            }

            double v = Double.valueOf(value);
            if (v >= min && v < max) {
                return woe;
            }
        } else {
            if (StringUtils.isEmpty(value)){
                value = "nan";
            }
            if (categoricalValue.contains(value)){
                return woe;
            }
        }
        return null;
    }

//    public static void main(String[] args) {
//        System.out.println((double)Long.MIN_VALUE);
//    }

}
