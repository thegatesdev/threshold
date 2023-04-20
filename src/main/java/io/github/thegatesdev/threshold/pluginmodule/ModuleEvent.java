package io.github.thegatesdev.threshold.pluginmodule;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModuleEvent<T> {
    private final List<Consumer<T>> listeners = new ArrayList<>();

    public void dispatch(T t) {
        for (int i = 0; i < listeners.size(); i++) listeners.get(i).accept(t);
    }

    public void bind(Consumer<T> consumer) {
        listeners.add(consumer);
    }

    public void unbind(Consumer<T> consumer) {
        listeners.remove(consumer);
    }
}
