package io.github.thegatesdev.threshold.pluginmodule;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class PluginModule<P extends JavaPlugin> {
    protected final String id;
    protected final P plugin;
    protected final ModuleManager<P> moduleManager;
    
    boolean isLoading = false,
            isLoaded = false,
            isEnabled = false,
            hasBeenLoaded = false,
            manualDisabled = false;

    public PluginModule(final String id, ModuleManager<P> moduleManager) {
        this.id = id;
        this.plugin = moduleManager.plugin();
        this.moduleManager = moduleManager;
    }

    protected void onLoad() {
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    protected void onUnload() {
    }

    protected void onFirstLoad() {
    }

    void enable() {
        assertLoaded();
        if (!isEnabled) {
            onEnable();
            isEnabled = true;
        }
    }

    void disable() {
        assertLoaded();
        if (isEnabled) {
            onDisable();
            isEnabled = false;
        }
    }

    void load() {
        if (isLoaded) throw new RuntimeException("Module is already loaded");
        isLoading = true;
        if (!hasBeenLoaded) {
            onFirstLoad();
            hasBeenLoaded = true;
        }
        onLoad();
        isLoading = false;
        isLoaded = true;
    }

    void unload() {
        assertLoaded();
        if (isEnabled) disable();
        onUnload();
        isLoaded = false;
    }

    public void assertLoaded() throws RuntimeException {
        if (!isLoaded) throw new RuntimeException("Module is not loaded");
    }

    public void assertEnabled() throws RuntimeException {
        if (!isEnabled) throw new RuntimeException("Module is not enabled");
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public String id() {
        return id;
    }
}
