package com.thegates.gatebase;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

public class GateBase extends JavaPlugin {

    public static Vector[] fromTo(Vector from, Vector to, double stepSize) {
        final int steps = (int) Math.floor(from.distance(to) / stepSize);
        final Vector[] array = new Vector[steps];
        final Vector dir = to.clone().subtract(from).normalize();
        for (int i = 0; i < steps; i++) {
            array[i] = from.clone().add(dir.clone().multiply(i * stepSize));
        }
        return array;
    }

    public static <E extends Enum<E>> Optional<E> enumGetOp(Class<E> clazz, String name) {
        return Optional.ofNullable(enumGet(clazz, name));
    }

    public static <E extends Enum<E>> E enumGet(Class<E> clazz, String name) {
        if (clazz == null || name == null || name.isBlank()) return null;
        final String convert = name.strip().replaceAll("\\s+", "").toUpperCase(Locale.ROOT);
        try {
            return Enum.valueOf(clazz, convert);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static <T> boolean forEachOR(Iterable<T> iterable, Predicate<T> predicate) {
        if (iterable == null || predicate == null) return false;
        for (T condition : iterable) {
            if (predicate.test(condition)) return true;
        }
        return false;
    }

    public static <T> boolean forEachAND(Iterable<T> iterable, Predicate<T> predicate) {
        if (iterable == null || predicate == null) return false;
        for (T condition : iterable) {
            if (!predicate.test(condition)) return false;
        }
        return true;
    }

    public static <T> boolean forEachORAll(Iterable<T> iterable, Predicate<T> predicate) {
        if (iterable == null || predicate == null) return false;
        boolean ret = false;
        for (T condition : iterable) {
            if (predicate.test(condition) && !ret) ret = true;
        }
        return ret;
    }

    public static <T> boolean forEachANDAll(Iterable<T> iterable, Predicate<T> predicate) {
        if (iterable == null || predicate == null) return false;
        boolean ret = true;
        for (T condition : iterable) {
            if (!predicate.test(condition) && ret) ret = false;
        }
        return ret;
    }
}
