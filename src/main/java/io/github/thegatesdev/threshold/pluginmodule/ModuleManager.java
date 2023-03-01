package io.github.thegatesdev.threshold.pluginmodule;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ModuleManager<P> {

    private final Map<Class<?>, PluginModule<P>> modules = new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    public <M extends PluginModule<P>> M getModule(Class<M> moduleClass) {
        final M module = (M) modules.get(moduleClass);
        if (module == null) throw new NullPointerException("This module does not exist");
        if (!module.isLoaded()) {
            if (module.isLoading) throw new RuntimeException("This module is still loading");
            // TODO Only load when boolean crossloading e.g. loadAll
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

    public void disableAll(Logger logger) {
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

    public void enableAll(Logger logger) {
        logger.info("Enabling all modules");
        for (final PluginModule<P> module : modules.values()) {

            if (module.isEnabled()) logger.info(module.id + " is already enabled.");
            else if (module.isLoaded()) {
                module.enable();
                logger.info(module.id + " has been enabled.");
            } else logger.warning(module.id + " is not loaded.");

        }
    }

    public void loadAll(Logger logger) {
        logger.info("Loading all modules");

        for (final PluginModule<P> module : modules.values()) {

            if (module.isLoaded() && !module.loadedByOther) logger.info(module.id + " has already been loaded.");
            else if (module.isLoading) logger.warning(module.id + " is already in loading process.");
            else {
                module.load();
                logger.info(module.id + " has been loaded");
            }
            module.loadedByOther = false;

        }
    }

    private void unloadAll(Logger logger) {
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

    public void reloadAll(Logger logger) {
        loadAll(logger);
        unloadAll(logger);
    }
}
