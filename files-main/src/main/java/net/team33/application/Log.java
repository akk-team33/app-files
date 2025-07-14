package net.team33.application;

import net.team33.application.logging.*;
import net.team33.messaging.multiplex.Router;

import java.util.HashMap;
import java.util.function.Consumer;

public final class Log {

    private static Formatter format = new PlainFormat();
    private static final Routing ROUTING = new Routing();

    private Log() {
    }

    public static void add(final Consumer<? extends Loggable> handler, final Level... levels) {
        for (final Level level : levels) {
            if (!ROUTING.containsKey(level)) {
                ROUTING.put(level, new Router<>());
            }
            ROUTING.get(level).add(handler);
        }
    }

    public static void put(final Loggable entry) {
        if (ROUTING.containsKey(entry.getLevel())) {
            ROUTING.get(entry.getLevel()).route(entry);
        }

    }

    public static void put(final Level level, final String text, final Throwable exception) {
        put(new Entry(level, Thread.currentThread(), text, exception));
    }

    public static void debug(final String text, final Throwable exception) {
        put(Level.DEBUG, text, exception);
    }

    public static void debug(final String text) {
        debug(text, null);
    }

    public static void debug(final Throwable exception) {
        debug(null, exception);
    }

    public static void info(final String text, final Throwable exception) {
        put(Level.INFO, text, exception);
    }

    public static void info(final String text) {
        info(text, null);
    }

    public static void info(final Throwable exception) {
        info(null, exception);
    }

    public static void warning(final String text, final Throwable exception) {
        put(Level.WARNING, text, exception);
    }

    public static void warning(final String text) {
        warning(text, null);
    }

    public static void warning(final Throwable exception) {
        warning(null, exception);
    }

    public static void error(final String text, final Throwable exception) {
        put(Level.ERROR, text, exception);
    }

    public static void error(final String text) {
        error(text, null);
    }

    public static void error(final Throwable exception) {
        error(null, exception);
    }

    public static void setDefaultUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    }

    public static Formatter getStdFormat() {
        return format;
    }

    public static void setStdFormat(final Formatter fmt) {
        format = fmt;
    }

    private static class Entry extends LogEntry implements Loggable {
        public Entry(final Level level, final Thread thread, final String text, final Throwable exception) {
            super(level, thread, text, exception);
        }
    }

    private static class Routing extends HashMap<Level, Router<Loggable>> {
    }

    private static class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public final void uncaughtException(final Thread thread, final Throwable exception) {
            Log.put(new Entry(Level.FATAL, thread, "Unbehandelte Ausnahme", exception));
        }
    }
}
