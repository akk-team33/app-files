//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.team33.fscalc.task;

import net.team33.messaging.Message;
import net.team33.messaging.multiplex.Originator;

public interface Task extends Runnable, Originator<Message<Task>> {
    Thread getThread();

    void quit();

    public interface Closure extends Message<Task> {
    }

    public interface Progress extends Message<Task> {
        String getPrefix();

        String getSubject();

        double getRatio();
    }
}
