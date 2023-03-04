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

    public <M extends PluginModule<P>> M get(Class<M> moduleClass) {
        M module = getStatic(moduleClass);
        if (!module.isLoaded()) {
            if (!canCrossLoad) throw new RuntimeException("This module is not loaded");
            if (module.isLoading) throw new RuntimeException("This module is still loading");
            module.loadedByOther = true;
            try {
                module.load();
                logger.info(module.id + " has been loaded indirectly");
            } catch (Exception e) {
                logger.warning(module.id + " failed to load (indirectly); " + e.getMessage());
            }
        }
        return module;
    }

    @SuppressWarnings("unchecked")
    public <M extends PluginModule<P>> M getStatic(Class<M> moduleClass) {
        final M module = (M) modules.get(moduleClass);
        if (module == null) throw new NullPointerException("This module does not exist");
        return module;
    }

    @SafeVarargs
    public final ModuleManager<P> add(PluginModule<P>... modules) {
        for (final PluginModule<P> module : modules) this.modules.putIfAbsent(module.getClass(), module);
        return this;
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
                try {
                    module.enable();
                    logger.info(module.id + " has been enabled.");
                } catch (Exception e) {
                    logger.warning(module.id + " failed to enable; " + e.getMessage());
                }
            } else logger.warning(module.id + " is not loaded.");

        }
    }

    private void loadAll() {
        logger.info("Loading all modules");
        canCrossLoad = true;
        for (final PluginModule<P> module : modules.values()) {

            if (module.isLoaded() && !module.loadedByOther) logger.info(module.id + " has already been loaded.");
            else if (module.isLoading) logger.warning(module.id + " is already in loading process.");
            else {
                try {
                    module.load();
                    logger.info(module.id + " has been loaded");
                } catch (Exception e) {
                    logger.warning(module.id + " failed to load; " + e.getMessage());
                }
            }
            module.loadedByOther = false;

        }
        canCrossLoad = false;
    }

    private void unloadAll() {
        logger.info("Unloading all modules");

        for (final PluginModule<P> module : modules.values()) {

            if (!module.isLoaded()) logger.info(module.id + " is not loaded.");
            else if (module.isLoading) logger.warning(module.id + " is in loading process.");
            else {
                try {
                    module.unload();
                    logger.info(module.id + " has been unloaded.");
                } catch (Exception e) {
                    logger.warning(module.id + " failed to unload; " + e.getMessage());
                }
            }

        }
    }

    public void reloadAll() {
        logger.info("Reloading modules...");
        final long before = System.currentTimeMillis();
        unloadAll();
        loadAll();
        logger.info("Reload complete: %sms".formatted(System.currentTimeMillis() - before));
    }
}
