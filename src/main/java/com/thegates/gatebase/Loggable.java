package com.thegates.gatebase;

public interface Loggable {
    void log(String message);

    default void log(String message, Level level) {
        log(message);
    }

    enum Level {
        NORMAL, IMPORTANT, WARNING, ERROR
    }
}
