package io.github.thegatesdev.threshold.pluginmodule;

public abstract class PluginModule<P> {

    protected final String id;
    protected final P plugin;

    protected boolean isLoaded = false, isEnabled = false;
    boolean isLoading = false, loadedByOther = false;

    public PluginModule(final String id, P plugin) {
        this.id = id;
        this.plugin = plugin;
    }

    protected abstract void onLoad();

    protected abstract void onEnable();

    protected void onDisable() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Module %s cannot be disabled".formatted(id));
    }

    protected void onUnload() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Modules %s cannot be unloaded".formatted(id));
    }

    void enable() {
        if (!isLoaded) throw new RuntimeException("Module is not loaded");
        if (!isEnabled) {
            onEnable();
            isEnabled = true;
        }
    }

    void disable() throws UnsupportedOperationException {
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

    void unload() throws UnsupportedOperationException {
        if (!isLoaded) throw new RuntimeException("Module is already unloaded");
        loadedByOther = false;
        isLoaded = false;
        onUnload();
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
