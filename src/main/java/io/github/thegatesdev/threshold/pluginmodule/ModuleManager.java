package io.github.thegatesdev.threshold.pluginmodule;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

public class ModuleManager<P extends JavaPlugin> {

    private final Map<Class<?>, PluginModule<P>> mappedModules = new HashMap<>();
    private final List<PluginModule<P>> modules = new ArrayList<>();

    private final Logger logger;
    private final P plugin;

    private boolean canCrossLoad = false;

    public ModuleManager(P plugin) {
        this.logger = plugin.getLogger();
        this.plugin = plugin;
    }

    public P plugin() {
        return plugin;
    }

    public Logger logger() {
        return logger;
    }

    public <M extends PluginModule<P>> M get(Class<M> moduleClass) {
        M module = getStatic(moduleClass);
        if (!module.isLoaded()) {
            if (!canCrossLoad) throw new RuntimeException("This module is not loaded");
            if (module.isLoading) throw new RuntimeException("This module is still loading");
            try {
                module.load();
            } catch (Exception e) {
                logger.warning(module.id + " failed to load (indirectly); " + e.getMessage());
            }
        }
        return module;
    }

    @SuppressWarnings("unchecked")
    public <M extends PluginModule<P>> M getStatic(Class<M> moduleClass) {
        final M module = (M) mappedModules.get(moduleClass);
        if (module == null) throw new NullPointerException("This module does not exist");
        return module;
    }

    @SafeVarargs
    public final ModuleManager<P> add(Function<ModuleManager<P>, PluginModule<P>>... moduleGenerators) {
        for (final Function<ModuleManager<P>, PluginModule<P>> moduleGenerator : moduleGenerators) {
            final var module = moduleGenerator.apply(this);
            if (this.mappedModules.putIfAbsent(module.getClass(), module) == null) this.modules.add(module);
        }
        return this;
    }

    public void enable(Class<? extends PluginModule<?>> moduleClass) {
        final PluginModule<P> module = mappedModules.get(moduleClass);
        if (module == null) throw new NullPointerException("This module does not exist");
        if (module.isLoaded() && !module.isEnabled()) {
            module.enable();
            module.manualDisabled = false;
        }
    }

    public void disable(Class<? extends PluginModule<?>> moduleClass) {
        final PluginModule<P> module = mappedModules.get(moduleClass);
        if (module == null) throw new NullPointerException("This module does not exist");
        if (module.isLoaded() && module.isEnabled()) {
            module.disable();
            module.manualDisabled = true;
        }
    }

    public synchronized void disable() {
        for (PluginModule<P> module : modules) if (module.isLoaded()) module.disable();
    }

    public synchronized void enable() {
        for (PluginModule<P> module : modules) if (!module.manualDisabled) module.enable();
    }

    public synchronized void initialize() {
        for (PluginModule<P> module : modules) if (!module.isInitialized) module.initialize();
    }

    public synchronized void load() {
        canCrossLoad = true;
        int i = 0;
        for (final PluginModule<P> module : modules) {
            if (module.isLoaded()) continue;

            if (module.isLoading) logger.warning(module.id + " is already in loading process.");
            else {
                try {
                    module.load();
                    i++;
                } catch (Exception e) {
                    logger.warning(module.id + " failed to load; " + e.getMessage());
                }
            }
        }
        canCrossLoad = false;
        logger.info("Loaded %s modules.".formatted(i));
    }

    public synchronized void unload() {
        int i = 0;
        for (final PluginModule<P> module : modules) {
            if (!module.isLoaded()) continue;

            if (module.isLoading) logger.warning(module.id + " is in loading process.");
            else {
                try {
                    module.unload();
                    i++;
                } catch (Exception e) {
                    logger.warning(module.id + " failed to unload; " + e.getMessage());
                }
            }
        }
        logger.info("Unloaded %s modules.".formatted(i));
    }
}
