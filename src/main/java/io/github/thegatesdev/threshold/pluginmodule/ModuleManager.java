package io.github.thegatesdev.threshold.pluginmodule;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;

public class ModuleManager<P extends JavaPlugin> implements Iterable<PluginModule<P>> {

    private final Map<Class<?>, PluginModule<P>> mappedModules = new HashMap<>();
    private final Map<String, PluginModule<P>> stringMappedModules = new HashMap<>();
    private final List<String> moduleKeys = new ArrayList<>();
    private final List<String> moduleKeysView = Collections.unmodifiableList(moduleKeys);

    private final List<PluginModule<P>> modules = new ArrayList<>();

    private final Logger logger;
    private final P plugin;

    private boolean canCrossLoad = false;

    public ModuleManager(P plugin) {
        this.logger = plugin.getLogger();
        this.plugin = plugin;
    }

    // -- MODULE

    public <M extends PluginModule<P>> M getLoaded(Class<M> moduleClass) {
        return getLoaded(get(moduleClass));
    }

    @Nullable
    public PluginModule<P> getLoaded(String key) {
        var module = get(key);
        if (module == null) return null;
        return getLoaded(module);
    }

    @SuppressWarnings("unchecked")
    public <M extends PluginModule<P>> M get(Class<M> moduleClass) {
        final M module = (M) mappedModules.get(moduleClass);
        if (module == null) throw new NullPointerException("This module does not exist");
        return module;
    }

    @Nullable
    public PluginModule<P> get(String moduleId) {
        return stringMappedModules.get(moduleId);
    }

    private <M extends PluginModule<P>> M getLoaded(M module) {
        if (!module.isLoaded()) {
            if (!canCrossLoad) throw new RuntimeException("This module is not loaded");
            if (module.isLoading()) throw new RuntimeException("This module is still loading");
            try {
                module.load();
            } catch (Exception e) {
                logger.warning(module.id + " failed to load (indirectly); " + e.getMessage());
            }
        }
        return module;
    }

    @SafeVarargs
    public final ModuleManager<P> add(Function<ModuleManager<P>, PluginModule<P>>... moduleGenerators) {
        for (final Function<ModuleManager<P>, PluginModule<P>> moduleGenerator : moduleGenerators) {
            final var module = moduleGenerator.apply(this);
            if (mappedModules.containsKey(module.getClass())) throw new RuntimeException("Duplicate module type");
            if (stringMappedModules.containsKey(module.id())) throw new RuntimeException("Duplicate module key");
            mappedModules.put(module.getClass(), module);
            stringMappedModules.put(module.id(), module);
            modules.add(module);
            moduleKeys.add(module.id());
        }
        return this;
    }

    // -- ACTIONS
    public synchronized void disable() {
        for (PluginModule<P> module : modules) if (module.isEnabled()) module.doDisable();
    }

    public synchronized void enable() {
        for (PluginModule<P> module : modules) if (module.isLoaded() && !module.isEnabled()) module.doEnable();
    }

    public synchronized void initialize() {
        for (PluginModule<P> module : modules) if (!module.isInitialized()) module.initialize();
    }

    public synchronized void load() {
        canCrossLoad = true;
        int i = 0;
        for (final PluginModule<P> module : modules) {
            if (module.isLoaded()) continue;

            if (module.isLoading()) logger.warning(module.id + " is already in loading process.");
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

            if (module.isLoading()) logger.warning(module.id + " is in loading process.");
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

    // -- GET/SET

    public void forEach(Consumer<? super PluginModule<P>> moduleConsumer) {
        mappedModules.values().forEach(moduleConsumer);
    }

    public P plugin() {
        return plugin;
    }

    public Logger logger() {
        return logger;
    }

    public List<String> moduleKeys() {
        return moduleKeysView;
    }

    public int moduleCount() {
        return modules.size();
    }

    @NotNull
    @Override
    public Iterator<PluginModule<P>> iterator() {
        return mappedModules.values().iterator();
    }
}
