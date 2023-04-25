package io.github.thegatesdev.threshold;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class PluginEvent<T> {
    private final List<Listener<T>> listeners = new ArrayList<>();

    public void dispatch(T t) {
        for (int i = 0; i < listeners.size(); i++) listeners.get(i).handle(t);
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
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
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
