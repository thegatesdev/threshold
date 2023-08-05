package io.github.thegatesdev.threshold.event.listening;

public interface ClassListener<Event> extends StaticListener<Event> {
    Class<Event> eventType();
}
