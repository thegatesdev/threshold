package io.github.thegatesdev;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

public class Threshold extends JavaPlugin {

    // MODULES

    private final Map<NamespacedKey, PluginModule> modules = new HashMap<>();

    public <M extends PluginModule> M addModule(M module) {
        modules.putIfAbsent(module.key(), module);
        return module;
    }

    public PluginModule getModule(NamespacedKey key) {
        return modules.get(key);
    }

    public boolean hasModule(NamespacedKey key) {
        return modules.containsKey(key);
    }

    public <M extends PluginModule> M getModule(NamespacedKey key, Class<M> moduleClass) {
        final PluginModule module = getModule(key);
        return moduleClass.isInstance(module) ? moduleClass.cast(module) : null;
    }

    // UTIL

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
        if (clazz == null || name == null || name.isBlank()) return null;
        final String convert = name.strip().replaceAll("\\s+", "").toUpperCase(Locale.ROOT);
        try {
            return Enum.valueOf(clazz, convert);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static <T> boolean forEachOR(Iterable<T> iterable, Predicate<T> predicate) {
        if (iterable == null || predicate == null) return true;
        for (T condition : iterable) {
            if (predicate.test(condition)) return true;
        }
        return false;
    }

    public static <T> boolean forEachAND(Iterable<T> iterable, Predicate<T> predicate) {
        if (iterable == null || predicate == null) return true;
        for (T condition : iterable) {
            if (!predicate.test(condition)) return false;
        }
        return true;
    }

    public static <T> boolean forEachORAll(Iterable<T> iterable, Predicate<T> predicate) {
        if (iterable == null || predicate == null) return true;
        boolean ret = false;
        for (T condition : iterable) {
            if (predicate.test(condition) && !ret) ret = true;
        }
        return ret;
    }

    public static <T> boolean forEachANDAll(Iterable<T> iterable, Predicate<T> predicate) {
        if (iterable == null || predicate == null) return true;
        boolean ret = true;
        for (T condition : iterable) {
            if (!predicate.test(condition) && ret) ret = false;
        }
        return ret;
    }
}
