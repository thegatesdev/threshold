package io.github.thegatesdev.threshold;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import java.util.Locale;
import java.util.UUID;
import java.util.function.Predicate;

public class Threshold {
    // UTIL

    public static double clampD(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    public static int clampI(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public static AttributeModifier fastModifier(Attribute attribute, double amount, EquipmentSlot equipmentSlot) {
        return new AttributeModifier(UUID.nameUUIDFromBytes((attribute.name() + amount).getBytes()), (attribute.name() + amount), amount, AttributeModifier.Operation.ADD_NUMBER, equipmentSlot);
    }

    public static AttributeModifier fastModifier(Attribute attribute, double amount) {
        return fastModifier(attribute, amount, null);
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
        final String convert = name.strip().replaceAll("\\s+", "").toUpperCase(Locale.ROOT);
        try {
            return Enum.valueOf(clazz, convert);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static <T> boolean forEachOR(Iterable<T> iterable, Predicate<T> predicate) {
        for (T condition : iterable) {
            if (predicate.test(condition)) return true;
        }
        return false;
    }

    public static <T> boolean forEachAND(Iterable<T> iterable, Predicate<T> predicate) {
        for (T condition : iterable) {
            if (!predicate.test(condition)) return false;
        }
        return true;
    }

    public static <T> boolean forEachORAll(Iterable<T> iterable, Predicate<T> predicate) {
        boolean ret = false;
        for (T condition : iterable) {
            if (predicate.test(condition)) ret = true;
        }
        return ret;
    }

    public static <T> boolean forEachANDAll(Iterable<T> iterable, Predicate<T> predicate) {
        boolean ret = true;
        for (T condition : iterable) {
            if (!predicate.test(condition)) ret = false;
        }
        return ret;
    }
}
