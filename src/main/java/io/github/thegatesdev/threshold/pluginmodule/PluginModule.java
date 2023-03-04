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

    protected void onLoad() {
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    protected void onUnload() {
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
        onLoad();
        isLoading = false;
        isLoaded = true;
    }

    void unload() {
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
}
