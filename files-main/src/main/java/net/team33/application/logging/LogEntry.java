package net.team33.application.logging;

import java.util.Date;

public class LogEntry {
    private final Thread thread;
    private final String text;
    private final Throwable exception;
    private final Date date = new Date();
    private final Level level;

    public LogEntry(Level level, Thread thread, String text, Throwable exception) {
        this.level = level;
        this.thread = thread;
        this.text = text;
        this.exception = exception;
    }

    public final Level getLevel() {
        return this.level;
    }

    public final Date getDate() {
        return this.date;
    }

    public final Throwable getException() {
        return this.exception;
    }

    public final String getText() {
        return this.text;
    }

    public final Thread getThread() {
        return this.thread;
    }
}
