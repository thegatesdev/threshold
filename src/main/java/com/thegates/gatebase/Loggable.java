package com.thegates.gatebase;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public final class Loggable {

    private List<Message> noConsumerLogs;
    private boolean hasListeners;
    private final List<Consumer<Message>> consumers = new LinkedList<>();

    public Loggable() {
        noConsumerLogs = new LinkedList<>();
        hasListeners = false;
    }

    public Loggable(Consumer<Message> consumer) {
        noConsumerLogs = null;
        consumers.add(consumer);
        hasListeners = true;
    }

    public void listen(Consumer<Message> consumer) {
        consumers.add(consumer);
        if (!hasListeners) {
            noConsumerLogs.forEach(consumer);
            noConsumerLogs = null;
            hasListeners = true;
        }
    }

    public void log(Message message) {
        if (hasListeners) {
            consumers.forEach(consumer -> consumer.accept(message));
        } else noConsumerLogs.add(message);
    }

    public void log(String message) {
        log(new Message(message, LogPrio.NORMAL));
    }

    public void log(String message, LogPrio prio) {
        log(new Message(message, prio));
    }

    public record Message(String message, LogPrio prio) {
        public String joined() {
            return prio.name() + ": " + message;
        }
    }

    enum LogPrio {
        BOTTOM(0), LOW(1), NORMAL(2), HIGH(3), TOP(4);
        public final int level;

        LogPrio(int level) {
            this.level = level;
        }

        public int compare(LogPrio other) {
            return Integer.compare(level, other.level);
        }
    }
}
