package io.github.thegatesdev.threshold.pluginmodule;

public abstract class PluginModule<P> {
    protected final String id;
    protected final P plugin;

    protected boolean isInitialized = false, isLoaded = false, isEnabled = false;
    boolean isLoading = false, isInitializing = false, loadedByOther = false;

    public PluginModule(final String id, P plugin) {
        this.id = id;
        this.plugin = plugin;
    }

    protected abstract void onInitialize();

    protected abstract void onLoad();

    protected abstract void onEnable();

    protected void onDisable() throws UnsupportedModuleOperationException {
        throw new UnsupportedOperationException("Module %s cannot be disabled".formatted(id));
    }

    protected void onUnload() throws UnsupportedModuleOperationException {
        throw new UnsupportedOperationException("Modules %s cannot be unloaded".formatted(id));
    }

    void initialize() {
        if (isInitialized) throw new RuntimeException("Module is already initialized");
        isInitializing = true;
        onInitialize();
        isInitialized = true;
        isInitializing = false;
    }

    void enable() {
        assertLoaded();
        if (!isEnabled) {
            onEnable();
            isEnabled = true;
        }
    }

    void disable() throws UnsupportedModuleOperationException {
        if (!isLoaded) throw new RuntimeException("Module is not loaded");
        if (isEnabled) {
            onDisable();
            isEnabled = false;
        }
    }

    void load() {
        if (isLoaded) throw new RuntimeException("Module is already loaded");
        isLoading = true;
        onLoad();
        isLoading = false;
        isLoaded = true;
    }

    void unload() throws UnsupportedModuleOperationException {
        assertLoaded();
        loadedByOther = false;
        isLoaded = false;
        disable();
        onUnload();
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

    public static class UnsupportedModuleOperationException extends UnsupportedOperationException {
    }
}
