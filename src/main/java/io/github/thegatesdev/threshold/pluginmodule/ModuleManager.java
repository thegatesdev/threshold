package io.github.thegatesdev.threshold.pluginmodule;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ModuleManager<P> {

    private final Map<Class<?>, PluginModule<P>> modules = new LinkedHashMap<>();
    private final Logger logger;

    private boolean canCrossLoad = false;

    public ModuleManager(Logger logger) {
        this.logger = logger;
    }

    @SuppressWarnings("unchecked")
    public <M extends PluginModule<P>> M getModule(Class<M> moduleClass) {
        final M module = (M) modules.get(moduleClass);
        if (module == null) throw new NullPointerException("This module does not exist");
        if (!module.isLoaded()) {
            if (!canCrossLoad) throw new RuntimeException("This module is not loaded");
            if (module.isLoading) throw new RuntimeException("This module is still loading");
            module.load();
            module.loadedByOther = true;
        }
        return module;
    }

    public <M extends PluginModule<P>> M addModule(M module) {
        modules.putIfAbsent(module.getClass(), module);
        return module;
    }

    public void enable(Class<? extends PluginModule<?>> moduleClass) {
        final PluginModule<P> module = modules.get(moduleClass);
        if (module == null) throw new NullPointerException("This module does not exist");
        if (module.isLoaded() && !module.isEnabled()) module.enable();
    }

    public void disable(Class<? extends PluginModule<?>> moduleClass) {
        final PluginModule<P> module = modules.get(moduleClass);
        if (module == null) throw new NullPointerException("This module does not exist");
        if (module.isLoaded() && module.isEnabled()) module.disable();
    }

    public void disableAll() {
        logger.info("Disabling all modules");
        for (final PluginModule<P> module : modules.values()) {

            if (module.isEnabled()) {
                try {
                    module.disable();
                    logger.info(module.id + " has been disabled.");
                } catch (UnsupportedOperationException e) {
                    logger.info(module.id + " cannot be disabled.");
                } catch (Exception e) {
                    logger.warning(module.id + " failed to disable; " + e.getMessage());
                }
            } else if (module.isLoaded()) logger.info(module.id + " is already disabled.");
            else logger.info(module.id + " is not loaded.");

        }
    }

    public void enableAll() {
        logger.info("Enabling all modules");
        for (final PluginModule<P> module : modules.values()) {

            if (module.isEnabled()) logger.info(module.id + " is already enabled.");
            else if (module.isLoaded()) {
                module.enable();
                logger.info(module.id + " has been enabled.");
            } else logger.warning(module.id + " is not loaded.");

        }
    }

    public void loadAll() {
        logger.info("Loading all modules");
        canCrossLoad = true;
        for (final PluginModule<P> module : modules.values()) {

            if (module.isLoaded() && !module.loadedByOther) logger.info(module.id + " has already been loaded.");
            else if (module.isLoading) logger.warning(module.id + " is already in loading process.");
            else {
                module.load();
                logger.info(module.id + " has been loaded");
            }
            module.loadedByOther = false;

        }
        canCrossLoad = false;
    }

    private void unloadAll() {
        logger.info("Unloading all modules");

        for (final PluginModule<P> module : modules.values()) {

            if (!module.isLoaded()) logger.info(module.id + " has already been unloaded.");
            else if (module.isLoading) logger.warning(module.id + " is in loading process.");
            else {
                module.unload();
                logger.info(module.id + " has been unloaded");
            }

        }
    }

    public void reloadAll() {
        unloadAll();
        loadAll();
    }
}
