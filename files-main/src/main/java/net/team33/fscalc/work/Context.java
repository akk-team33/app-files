package net.team33.fscalc.work;

import de.team33.patterns.serving.alpha.Variable;
import net.team33.fscalc.info.FileInfo;
import net.team33.fscalc.task.Task;
import net.team33.fscalc.task.impl.Calculator;
import net.team33.fscalc.task.impl.Deletion;
import net.team33.messaging.Message;
import net.team33.messaging.multiplex.Originator;

import java.io.File;
import java.nio.file.Path;

public interface Context extends Originator<Message<Context>> {

    Variable<Path> path();

    Order getOrder();

    void setOrder(Order var1);

    Calculator startCalculation(File var1);

    Calculator startCalculation(FileInfo var1);

    Deletion startDeletion(File[] var1);

    public interface MsgChOrder extends Message<Context> {
        Order getOrder();
    }

    public interface MsgStarting extends Message<Context> {
        Task getTask();
    }
}
