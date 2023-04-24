package io.github.thegatesdev.threshold.pluginmodule;

public abstract class PluginModule<P> {
    protected final String id;
    protected final P plugin;

    protected boolean
            isLoaded = false,
            isEnabled = false,
            hasBeenLoaded = false;
    boolean isLoading = false,
            enabledBeforeUnload = false;

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

    protected void onFirstLoad() {
    }

    void enable() {
        assertLoaded();
        if (!isEnabled) {
            onEnable();
            isEnabled = true;
            enabledBeforeUnload = false;
        }
    }

    void disable() {
        assertLoaded();
        if (isEnabled) {
            onDisable();
            isEnabled = false;
            enabledBeforeUnload = false;
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
        enabledBeforeUnload = false;
    }

    void unload() {
        assertLoaded();
        if (isEnabled) {
            disable();
            enabledBeforeUnload = true;
        }
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
