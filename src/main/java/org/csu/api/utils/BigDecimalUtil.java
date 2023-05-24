package org.csu.api.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalUtil {

    private BigDecimalUtil() {}

    public static BigDecimal add(double v1, double v2) {
        return new BigDecimal(Double.toString(v1)).add(new BigDecimal(Double.toString(v2)));
    }

    public static BigDecimal subtract(double v1, double v2) {
        return new BigDecimal(Double.toString(v1)).subtract(new BigDecimal(Double.toString(v2)));
    }

    public static BigDecimal multiply(double v1, double v2) {
        return new BigDecimal(Double.toString(v1)).multiply(new BigDecimal(Double.toString(v2)));
    }

    public static BigDecimal divide(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));

        return b1.divide(b2, 2, RoundingMode.HALF_UP); // 四舍五入，保留两位小数
    }
}
