package io.github.thegatesdev.util;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class FastListener implements Listener {

    public void register(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
