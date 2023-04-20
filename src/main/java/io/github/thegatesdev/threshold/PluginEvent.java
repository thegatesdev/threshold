package io.github.thegatesdev.threshold;

import java.util.ArrayList;
import java.util.List;

public class PluginEvent<T> {
    private final List<Listener<T>> listeners = new ArrayList<>();

    public void dispatch(T t) {
        for (int i = 0; i < listeners.size(); i++) listeners.get(i).handle(t);
    }

    public void bind(Listener<T> listener) {
        listeners.add(listener);
    }

    public void unbind(Listener<T> listener) {
        listeners.remove(listener);
    }

    @FunctionalInterface
    public interface Listener<T> {
        void handle(T data);
    }
}
