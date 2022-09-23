package com.thegates.gatebase;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Collection;
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

    public static <E extends Enum<E>> Optional<E> enumGet(Class<E> clazz, String name) {
        if (clazz == null || name == null || name.isBlank()) return Optional.empty();
        final String convert = name.strip().replaceAll("\\s+", "").toUpperCase(Locale.ROOT);
        try {
            return Optional.of(Enum.valueOf(clazz, convert));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public static <T> boolean forEachOR(Collection<T> collection, Predicate<T> predicate) {
        for (T condition : collection) {
            if (predicate.test(condition)) return true;
        }
        return false;
    }

    public static <T> boolean forEachAND(Collection<T> collection, Predicate<T> predicate) {
        for (T condition : collection) {
            if (!predicate.test(condition)) return false;
        }
        return true;
    }
}
