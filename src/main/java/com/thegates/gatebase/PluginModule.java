package com.thegates.gatebase;

import org.bukkit.plugin.Plugin;

public interface PluginModule {
    boolean isEnabled();

    void enable(Plugin plugin);

    void disable(Plugin plugin);
}
