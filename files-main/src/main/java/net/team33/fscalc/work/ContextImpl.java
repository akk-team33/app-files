package net.team33.fscalc.work;

import de.team33.patterns.serving.alpha.Component;
import de.team33.patterns.serving.alpha.Variable;
import net.team33.application.Log;
import net.team33.fscalc.info.FileInfo;
import net.team33.fscalc.info.FileService;
import net.team33.fscalc.task.Task;
import net.team33.fscalc.task.impl.Calculator;
import net.team33.fscalc.task.impl.Deletion;
import net.team33.messaging.Message;
import net.team33.messaging.multiplex.Sender;

import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class ContextImpl extends Sender<Message<Context>> implements Context {

    private static final UnaryOperator<Path> NORMAL_PATH = path -> path.toAbsolutePath().normalize();

    private final Component<Order> order;
    private final Component<Path> path;
    private final List<Calculator> calculators = new Vector<>(2, 2);
    private static int tCount = 0;

    public ContextImpl(final File path, final Order order) {
        this.path = new Component<>(NORMAL_PATH, path.toPath());
        this.order = new Component<>(order);

        this.path.retrieve(p -> startCalculation(p.toFile()));
        //this.order.subscribe(o -> new MSG_CHORDER());
    }

    @Override
    public final Variable<Path> path() {
        return path;
    }

    @Override
    public Variable<Order> order() {
        return order;
    }

    @Override
    public final Calculator startCalculation(final File path) {
        return startCalculation(FileService.getInstance().getInfo(path));
    }

    @Override
    public final Calculator startCalculation(final FileInfo info) {
        final Vector calcopy;
        synchronized (calculators) {
            calcopy = new Vector(calculators);
        }

        final Iterator var4 = calcopy.iterator();

        Calculator ret;
        while(var4.hasNext()) {
            ret = (Calculator)var4.next();
            ret.quit();
        }

        ret = new Calculator(info);
        synchronized (calculators) {
            calculators.add(ret);
            Log.debug(this + ".calculators.size() = " + calculators.size());
        }

        start(ret).getRegister().add(new LSN_CLS_CALC());
        return ret;
    }

    @Override
    public final Deletion startDeletion(final File[] paths) {
        final Deletion ret = start(new Deletion(paths));
        ret.getRegister().add(new LSN_CLOSURE());
        return ret;
    }

    private <T extends Task> T start(final T task) {
        fire(new MSG_STARTING(task));
        final Thread th = new Thread(task, task.getClass().getName() + "-" + ++tCount);
        th.start();
        return task;
    }

    private class LSN_CLOSURE implements Consumer<Task.Closure> {

        @Override
        public final void accept(final Task.Closure message) {
            message.getSender().getRegister().remove(this);
            startCalculation(path.get().toFile());
        }
    }

    private class LSN_CLS_CALC implements Consumer<Task.Closure> {

        @Override
        public final void accept(final Task.Closure message) {
            message.getSender().getRegister().remove(this);
            synchronized (calculators) {
                calculators.remove(message.getSender());
            }
        }
    }

    private class MSG_BASE implements Message<Context> {

        @Override
        public final Context getSender() {
            return ContextImpl.this;
        }
    }

    private class MSG_STARTING extends MSG_BASE implements Context.MsgStarting {
        private final Task task;

        private MSG_STARTING(final Task task) {
            addInitial(this);
            (this.task = task).getRegister().add(new LSN_CLOSURE());
        }

        @Override
        public final Task getTask() {
            return task;
        }

        private class LSN_CLOSURE implements Consumer<Task.Closure> {

            @Override
            public final void accept(final Task.Closure message) {
                message.getSender().getRegister().remove(this);
                removeInitial(MSG_STARTING.this);
            }
        }
    }
}
