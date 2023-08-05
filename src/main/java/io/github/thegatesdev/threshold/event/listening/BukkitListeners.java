package io.github.thegatesdev.threshold.event.listening;

import org.bukkit.event.*;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class BukkitListeners implements Listeners {
    private final Plugin plugin;
    private final Map<Class<?>, EventHandler<?>> mappedHandlers = new HashMap<>();

    private boolean handleEvents = true;
    private boolean cancelAll = false;

    public BukkitListeners(Plugin plugin) {
        this.plugin = plugin;
    }

    private static HandlerList handlerList(Class<?> eventClass) {
        if (Modifier.isInterface(eventClass.getModifiers())) return null;
        final Method method;
        try {
            method = eventClass.getDeclaredMethod("getHandlerList");
            method.setAccessible(true);
        } catch (NoSuchMethodException | SecurityException ignored) {
            return null;
        }
        Object o;
        try {
            o = method.invoke(null);
        } catch (IllegalAccessException | InvocationTargetException ignored) {
            return null;
        }
        return (HandlerList) o;
    }


    @Override
    public <E> void listen(Class<E> eventClass, StaticListener<E> listener) {
        @SuppressWarnings("unchecked")
        var handler = (EventHandler<E>) mappedHandlers.computeIfAbsent(eventClass, this::registerHandler);
        if (handler == null) throw new IllegalArgumentException(eventClass.getSimpleName() + " cannot be listened to");
        handler.add(listener);
    }

    @Override
    public void stop(Class<?> eventClass, StaticListener<?> listener) {
        var handler = mappedHandlers.get(eventClass);
        if (handler == null) return;
        handler.remove(listener);
    }

    @Override
    public void handleEvents(boolean handle) {
        this.handleEvents = handle;
    }

    public void cancelAll(boolean cancelAll) {
        this.cancelAll = cancelAll;
    }

    private <E> EventHandler<E> registerHandler(Class<E> eventClass) {
        var list = handlerList(eventClass);
        if (list == null) return null;
        var handler = new EventHandler<E>();
        list.register(new RegisteredListener(handler, handler, EventPriority.NORMAL, plugin, false));
        return handler;
    }

    private class EventHandler<ForEvent> implements Listener, EventExecutor {
        private final List<StaticListener<ForEvent>> listeners = new ArrayList<>();
        private final Set<StaticListener<ForEvent>> listenerSet = new HashSet<>();

        public void add(StaticListener<ForEvent> listener) {
            if (listenerSet.add(listener)) listeners.add(listener);
        }

        public void remove(StaticListener<?> listener) {
            if (listenerSet.remove(listener)) listeners.remove(listener);
        }

        @Override
        public void execute(@NotNull Listener listener, @NotNull Event event) {
            if (!handleEvents) return;
            @SuppressWarnings("unchecked")
            ForEvent castEvent = ((ForEvent) event);

            for (int i = 0; i < listeners.size(); i++)
                listeners.get(i).onEvent(castEvent);

            if (cancelAll && event instanceof Cancellable c) c.setCancelled(true);
        }
    }
}
