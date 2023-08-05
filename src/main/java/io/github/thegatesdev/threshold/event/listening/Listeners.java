package io.github.thegatesdev.threshold.event.listening;

public interface Listeners {
    <E> void listen(Class<E> eventClass, StaticListener<E> listener);

    default <E> void listen(ClassListener<E> listener) {
        listen(listener.eventType(), listener);
    }

    void stop(Class<?> eventClass, StaticListener<?> listener);

    void handleEvents(boolean handle);
}
