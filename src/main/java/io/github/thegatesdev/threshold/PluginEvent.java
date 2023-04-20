package io.github.thegatesdev.threshold;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PluginEvent<T> {
    private final List<Consumer<T>> listeners = new ArrayList<>();
    private final Consumer<Exception> errorHandler;

    public PluginEvent(final Consumer<Exception> errorHandler) {
        this.errorHandler = errorHandler;
    }

    public PluginEvent() {
        this(null);
    }

    public void dispatch(T t) {
        if (errorHandler == null) for (int i = 0; i < listeners.size(); i++) listeners.get(i).accept(t);
        else {
            for (int i = 0; i < listeners.size(); i++) {
                try {
                    listeners.get(i).accept(t);
                } catch (Exception e) {
                    errorHandler.accept(e);
                }
            }
        }
    }

    public void bind(Consumer<T> consumer) {
        listeners.add(consumer);
    }

    public void unbind(Consumer<T> consumer) {
        listeners.remove(consumer);
    }
}
