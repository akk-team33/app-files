//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.team33.application.logging;

import java.util.Date;

public class LogEntry {
    private Thread thread;
    private String text;
    private Throwable exception;
    private Date date = new Date();
    private Level level;

    public LogEntry(Level level, Thread thread, String text, Throwable exception) {
        this.level = level;
        this.thread = thread;
        this.text = text;
        this.exception = exception;
    }

    public Level getLevel() {
        return this.level;
    }

    public Date getDate() {
        return this.date;
    }

    public Throwable getException() {
        return this.exception;
    }

    public String getText() {
        return this.text;
    }

    public Thread getThread() {
        return this.thread;
    }
}
