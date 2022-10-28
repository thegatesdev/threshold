package com.thegates.gatebase.logging;

import com.thegates.gatebase.util.MathUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public final class Loggable {

    private List<Message> noConsumerLogs;
    private boolean hasListeners;
    private final List<Consumer<Message>> consumers = new LinkedList<>();
    private int logLevel = LogPrio.NORMAL.level;

    public Loggable() {
        noConsumerLogs = new LinkedList<>();
        hasListeners = false;
    }

    public Loggable(Consumer<Message> consumer) {
        noConsumerLogs = null;
        consumers.add(consumer);
        hasListeners = true;
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = MathUtil.clampI(logLevel, LogPrio.min(), LogPrio.max());
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
        if (message.prio.level < logLevel) return;
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
}
