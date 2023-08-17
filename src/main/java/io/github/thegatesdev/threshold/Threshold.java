package io.github.thegatesdev.threshold;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Locale;

public class Threshold extends JavaPlugin {
    // UTIL

    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    public static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public static Vector[] fromTo(Vector from, Vector to, double stepSize) {
        final int steps = (int) Math.floor(from.distance(to) / stepSize);
        final Vector[] array = new Vector[steps];
        final Vector dir = to.clone().subtract(from).normalize();
        for (int i = 0; i < steps; i++) {
            array[i] = from.clone().add(dir.clone().multiply(i * stepSize));
        }
        return array;
    }

    public static <E extends Enum<E>> E enumGet(Class<E> clazz, String name) {
        if (clazz == null || name == null) return null;
        try {
            return enumGetThrow(clazz, name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static <E extends Enum<E>> E enumGetThrow(Class<E> clazz, String name) throws IllegalArgumentException {
        return Enum.valueOf(clazz, name.strip().replaceAll("\\s+", "_").toUpperCase(Locale.ROOT));
    }

    public static <E extends Enum<E>> String[] enumNames(Class<E> enumClass) {
        final E[] constants = enumClass.getEnumConstants();
        final int len = constants.length;
        final String[] out = new String[len];
        for (int i = 0; i < len; i++) out[i] = constants[i].name();
        return out;
    }
}
