package com.thegates.gatebase;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class GateBase extends JavaPlugin {

    public Vector[] fromTo(Vector from, Vector to, double step) {
        final int steps = (int) Math.ceil(from.distance(to) / step);
        final Vector[] array = new Vector[steps];
        final Vector dir = from.clone().subtract(to);
        for (int i = 0; i <= steps; i++) {
            array[i] = dir.clone().multiply(i * step);
        }
        return array;
    }
}
