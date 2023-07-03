package io.github.thegatesdev.threshold.util;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public interface FastListener extends Listener {

    default void register(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
