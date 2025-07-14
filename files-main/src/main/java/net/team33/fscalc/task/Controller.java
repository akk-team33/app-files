package net.team33.fscalc.task;

public interface Controller {
    Controller getSubController(long var1);

    void increment(String var1, long var2);

    boolean isQuitting();
}
