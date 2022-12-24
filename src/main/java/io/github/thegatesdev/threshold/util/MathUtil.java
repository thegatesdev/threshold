package io.github.thegatesdev.threshold.util;

public class MathUtil {
    public static double clampD(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    public static int clampI(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }
}
