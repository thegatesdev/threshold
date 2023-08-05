package io.github.thegatesdev.threshold.event.listening;

@FunctionalInterface
public interface StaticListener<Event> {

    void onEvent(Event event);
}
