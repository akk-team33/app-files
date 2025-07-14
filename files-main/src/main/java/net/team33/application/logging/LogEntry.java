package net.team33.application.logging;

import java.util.Date;

public class LogEntry {
    private final Thread thread;
    private final String text;
    private final Throwable exception;
    private final Date date = new Date();
    private final Level level;

    public LogEntry(final Level level, final Thread thread, final String text, final Throwable exception) {
        this.level = level;
        this.thread = thread;
        this.text = text;
        this.exception = exception;
    }

    public final Level getLevel() {
        return level;
    }

    public final Date getDate() {
        return date;
    }

    public final Throwable getException() {
        return exception;
    }

    public final String getText() {
        return text;
    }

    public final Thread getThread() {
        return thread;
    }
}
