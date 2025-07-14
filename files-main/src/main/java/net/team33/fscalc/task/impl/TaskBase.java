package net.team33.fscalc.task.impl;

import net.team33.application.Log;
import net.team33.fscalc.task.Task;
import net.team33.messaging.Message;
import net.team33.messaging.multiplex.Sender;

public abstract class TaskBase extends Sender<Message<Task>> implements Task {
    private Thread thread = null;
    private boolean quit = false;

    protected final void fireProgress(final String subject, final double ratio) {
        fire(new PROGRESS(subject, ratio));
    }

    protected final boolean isQuit() {
        return quit;
    }

    protected abstract String getProgressPrefix();

    protected abstract void run_core();

    @Override
    public final Thread getThread() {
        return thread;
    }

    @Override
    public final void run() {
        if (thread == null) {
            try {
                this.thread = Thread.currentThread();
                run_core();
            } catch (final Throwable var2) {
                Log.error(var2);
            }

            final Message<Task> msg = new CLOSURE();
            addInitial(msg);
            fire(msg);
        } else {
            throw new RuntimeException("Es wurde versucht, die Task-Instanz '" + this + "' mehrmals auszuführen!");
        }
    }

    @Override
    public final void quit() {
        this.quit = true;
    }

    private class CLOSURE extends MessageBase implements Task.Closure {
    }

    private class MessageBase implements Message<Task> {

        @Override
        public final Task getSender() {
            return TaskBase.this;
        }
    }

    private class PROGRESS extends MessageBase implements Task.Progress {
        private final String subject;
        private final double ratio;

        public PROGRESS(final String subject, final double ratio) {
            this.subject = subject;
            this.ratio = ratio;
        }

        @Override
        public final String getPrefix() {
            return getProgressPrefix();
        }

        @Override
        public final double getRatio() {
            return ratio;
        }

        @Override
        public final String getSubject() {
            return subject;
        }
    }
}
