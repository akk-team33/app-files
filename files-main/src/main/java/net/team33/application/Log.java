package net.team33.application;

import net.team33.application.logging.*;
import net.team33.messaging.multiplex.Router;

import java.util.HashMap;
import java.util.function.Consumer;

public class Log {
    private static Formatter format = new PlainFormat();
    private static final Routing routing = new Routing();

    public static void add(Consumer<? extends Loggable> handler, Level... levels) {
        Level[] var5 = levels;
        int var4 = levels.length;

        for(int var3 = 0; var3 < var4; ++var3) {
            Level level = var5[var3];
            if (!routing.containsKey(level)) {
                routing.put(level, new Router());
            }

            ((Router)routing.get(level)).add(handler);
        }

    }

    public static void remove(Consumer<? extends Loggable> listener) {
        Level[] var4;
        int var3 = (var4 = Level.values()).length;

        for(int var2 = 0; var2 < var3; ++var2) {
            Level level = var4[var2];
            if (routing.containsKey(level)) {
                ((Router)routing.get(level)).remove(listener);
            }
        }

    }

    public static void put(Loggable entry) {
        if (routing.containsKey(entry.getLevel())) {
            ((Router)routing.get(entry.getLevel())).route(entry);
        }

    }

    public static void put(Level level, String text, Throwable exception) {
        put(new Entry(level, Thread.currentThread(), text, exception));
    }

    public static void debug(String text, Throwable exception) {
        put(Level.DEBUG, text, exception);
    }

    public static void debug(String text) {
        debug(text, (Throwable)null);
    }

    public static void debug(Throwable exception) {
        debug((String)null, exception);
    }

    public static void info(String text, Throwable exception) {
        put(Level.INFO, text, exception);
    }

    public static void info(String text) {
        info(text, (Throwable)null);
    }

    public static void info(Throwable exception) {
        info((String)null, exception);
    }

    public static void warning(String text, Throwable exception) {
        put(Level.WARNING, text, exception);
    }

    public static void warning(String text) {
        warning(text, (Throwable)null);
    }

    public static void warning(Throwable exception) {
        warning((String)null, exception);
    }

    public static void error(String text, Throwable exception) {
        put(Level.ERROR, text, exception);
    }

    public static void error(String text) {
        error(text, (Throwable)null);
    }

    public static void error(Throwable exception) {
        error((String)null, exception);
    }

    public static void setDefaultUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    }

    public static Formatter getStdFormat() {
        return format;
    }

    public static void setStdFormat(Formatter fmt) {
        format = fmt;
    }

    private static class Entry extends LogEntry implements Loggable {
        public Entry(Level level, Thread thread, String text, Throwable exception) {
            super(level, thread, text, exception);
        }
    }

    private static class Routing extends HashMap<Level, Router<Loggable>> {
    }

    private static class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public final void uncaughtException(Thread thread, Throwable exception) {
            Log.put(new Entry(Level.FATAL, thread, "Unbehandelte Ausnahme", exception));
        }
    }
}
