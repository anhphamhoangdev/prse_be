package com.hcmute.prse_be.util;

public class CalculateUtils {
    public static double calculateGrowthRate(double current, double previous) {
        // If previous is 0, set base to 1 to avoid division by zero
//        double base = (previous == 0) ? 1.0 : previous;
//        return ConvertUtils.toDouble((current - base) / base * 100);
        return ((previous == 0) ? 0.0 : (current - previous) / previous * 100);
    }
}
