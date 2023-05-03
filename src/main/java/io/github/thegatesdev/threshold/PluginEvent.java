package io.github.thegatesdev.threshold;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class PluginEvent<T> {
    private final List<Listener<T>> listeners = new ArrayList<>();
    private final Consumer<Exception> errorHandler;

    public PluginEvent(Consumer<Exception> errorHandler) {
        this.errorHandler = errorHandler;
    }

    public PluginEvent() {
        this(null);
    }

    public void dispatch(T t) {
        for (int i = 0, size = listeners.size(); i < size; i++) {
            try {
                listeners.get(i).handle(t);
            } catch (Exception e) {
                if (errorHandler != null) errorHandler.accept(e);
            }
        }
    }

    public void dispatchAsync(T t, ExecutorService executor) {
        final Future<?>[] futures = new Future[listeners.size()];
        for (int i = 0; i < listeners.size(); i++) {
            final int f = i;
            futures[f] = executor.submit(() -> listeners.get(f).handle(t));
        }
        for (int i = 0, futuresLength = futures.length; i < futuresLength; i++) {
            try {
                futures[i].get();
            } catch (InterruptedException ignored) {
            } catch (Exception e) {
                if (errorHandler != null) errorHandler.accept(e);
            }
        }
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
