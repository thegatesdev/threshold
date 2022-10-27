package com.thegates.gatebase.logging;

public enum LogPrio {
    BOTTOM(0), LOW(1), NORMAL(2), HIGH(3), TOP(4);
    public final int level;

    LogPrio(int level) {
        this.level = level;
    }

    public int compare(LogPrio other) {
        return Integer.compare(level, other.level);
    }

    public int max() {
        return TOP.level;
    }

    public int min() {
        return BOTTOM.level;
    }
}
