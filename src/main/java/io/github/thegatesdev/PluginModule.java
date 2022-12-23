package io.github.thegatesdev;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public interface PluginModule {
    boolean isEnabled();

    void enable(Plugin plugin);

    void disable(Plugin plugin);

    NamespacedKey key();
}
