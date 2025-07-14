package net.team33.fscalc.work;

import net.team33.application.Log;
import net.team33.fscalc.info.FileInfo;
import net.team33.fscalc.info.FileService;
import net.team33.fscalc.task.Task;
import net.team33.fscalc.task.impl.Calculator;
import net.team33.fscalc.task.impl.Deletion;
import net.team33.messaging.Listener;
import net.team33.messaging.Message;
import net.team33.messaging.multiplex.Sender;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class ContextImpl extends Sender<Message<Context>> implements Context {
    private Order order;
    private File path;
    private final List<Calculator> calculators = new Vector(2, 2);
    private static int tCount = 0;

    public ContextImpl(File path, Order order) {
        this.path = newFile(path);
        this.order = order;
        this.addInitial(new MSG_CHDIR());
        this.startCalculation(this.path);
    }

    @Override
    public final Order getOrder() {
        return this.order;
    }

    @Override
    public final void setOrder(Order ord) {
        if (this.order != ord) {
            this.order = ord;
            this.fire(new MSG_CHORDER());
        }

    }

    @Override
    public final File getPath() {
        return this.path;
    }

    @Override
    public final void setPath(File path) {
        path = newFile(path);
        if (!path.equals(this.path) && path.isDirectory()) {
            this.path = path;
            this.fire(new MSG_CHDIR());
            this.startCalculation(path);
        }

    }

    @Override
    public final Calculator startCalculation(File path) {
        return this.startCalculation(FileService.getInstance().getInfo(path));
    }

    @Override
    public final Calculator startCalculation(FileInfo info) {
        Vector calcopy;
        synchronized(this.calculators) {
            calcopy = new Vector(this.calculators);
        }

        Iterator var4 = calcopy.iterator();

        Calculator ret;
        while(var4.hasNext()) {
            ret = (Calculator)var4.next();
            ret.quit();
        }

        ret = new Calculator(info);
        synchronized(this.calculators) {
            this.calculators.add(ret);
            Log.debug(this + ".calculators.size() = " + this.calculators.size());
        }

        ((Calculator)this.start(ret)).getRegister().add(new LSN_CLS_CALC());
        return ret;
    }

    @Override
    public final Deletion startDeletion(File[] paths) {
        Deletion ret = (Deletion)this.start(new Deletion(paths));
        ret.getRegister().add(new LSN_CLOSURE());
        return ret;
    }

    private static File newFile(File path) {
        try {
            return path.getCanonicalFile();
        } catch (IOException var2) {
            Log.warning(var2);
            return path.getAbsoluteFile();
        }
    }

    private <T extends Task> T start(T task) {
        this.fire(new MSG_STARTING(task));
        Thread th = new Thread(task, task.getClass().getName() + "-" + ++tCount);
        th.start();
        return task;
    }

    private class LSN_CLOSURE implements Listener<Task.Closure> {

        @Override
        public final void pass(Task.Closure message) {
            ((Task)message.getSender()).getRegister().remove(this);
            ContextImpl.this.startCalculation(ContextImpl.this.getPath());
        }
    }

    private class LSN_CLS_CALC implements Listener<Task.Closure> {

        @Override
        public final void pass(Task.Closure message) {
            ((Task)message.getSender()).getRegister().remove(this);
            synchronized(ContextImpl.this.calculators) {
                ContextImpl.this.calculators.remove(message.getSender());
            }
        }
    }

    private class MSG_BASE implements Message<Context> {

        @Override
        public final Context getSender() {
            return ContextImpl.this;
        }
    }

    private class MSG_CHDIR extends MSG_BASE implements Context.MsgChDir {

        @Override
        public final File getPath() {
            return ContextImpl.this.getPath();
        }
    }

    private class MSG_CHORDER extends MSG_BASE implements Context.MsgChOrder {

        @Override
        public final Order getOrder() {
            return ContextImpl.this.getOrder();
        }
    }

    private class MSG_STARTING extends MSG_BASE implements Context.MsgStarting {
        private final Task task;

        private MSG_STARTING(Task task) {
            ContextImpl.this.addInitial(this);
            (this.task = task).getRegister().add(new LSN_CLOSURE());
        }

        @Override
        public final Task getTask() {
            return this.task;
        }

        private class LSN_CLOSURE implements Listener<Task.Closure> {

            @Override
            public final void pass(Task.Closure message) {
                ((Task)message.getSender()).getRegister().remove(this);
                ContextImpl.this.removeInitial(MSG_STARTING.this);
            }
        }
    }
}
