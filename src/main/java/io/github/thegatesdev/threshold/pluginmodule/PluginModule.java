package io.github.thegatesdev.threshold.pluginmodule;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public abstract class PluginModule<P extends JavaPlugin> {
    protected final String id;
    protected final P plugin;
    protected final ModuleManager<P> moduleManager;
    protected final Logger logger;

    boolean isLoading = false,
            isLoaded = false,
            isEnabled = false,
            isInitialized = false,
            manualDisabled = false;
    private boolean hasBeenLoaded = false;

    public PluginModule(final String id, ModuleManager<P> moduleManager) {
        this.id = id;
        this.plugin = moduleManager.plugin();
        this.logger = moduleManager.logger();
        this.moduleManager = moduleManager;
    }

    protected void onLoad() {
    }

    protected void onFirstLoad() {
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    protected void onUnload() {
    }

    protected void onInitialize() {
    }

    void enable() {
        assertLoaded();
        assertNotEnabled();
        onEnable();
        isEnabled = true;
    }

    void disable() {
        assertLoaded();
        assertEnabled();
        onDisable();
        isEnabled = false;
    }

    void load() {
        assertInitialized();
        assertNotLoaded();
        isLoading = true;
        if (!hasBeenLoaded) {
            onFirstLoad();
            hasBeenLoaded = true;
        }
        onLoad();
        isLoading = false;
        isLoaded = true;
    }

    void initialize() {
        assertNotInitialized();
        onInitialize();
        isInitialized = true;
    }

    void unload() {
        assertLoaded();
        if (isEnabled) disable();
        onUnload();
        isLoaded = false;
    }


    protected void assertInitialized() throws RuntimeException {
        if (!isInitialized) throw new RuntimeException("Module is not initialized");
    }

    protected void assertLoaded() throws RuntimeException {
        if (!isLoaded) throw new RuntimeException("Module is not loaded");
    }

    protected void assertEnabled() throws RuntimeException {
        if (!isEnabled) throw new RuntimeException("Module is not enabled");
    }

    protected void assertNotInitialized() throws RuntimeException {
        if (isInitialized) throw new RuntimeException("Module is already initialized");
    }

    protected void assertNotLoaded() throws RuntimeException {
        if (isLoaded) throw new RuntimeException("Module is already loaded");
        if (isLoading) throw new RuntimeException("Module is loading");
    }

    protected void assertNotEnabled() throws RuntimeException {
        if (isEnabled) throw new RuntimeException("Module is already enabled");
    }


    public boolean isLoading() {
        return isLoading;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public boolean manuallyDisabled() {
        return manualDisabled;
    }


    public String id() {
        return id;
    }
}
