//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.team33.fscalc.work;

import net.team33.fscalc.info.FileInfo;
import net.team33.fscalc.task.Task;
import net.team33.fscalc.task.impl.Calculator;
import net.team33.fscalc.task.impl.Deletion;
import net.team33.messaging.Message;
import net.team33.messaging.multiplex.Originator;

import java.io.File;

public interface Context extends Originator<Message<Context>> {
    File getPath();

    void setPath(File var1);

    Order getOrder();

    void setOrder(Order var1);

    Calculator startCalculation(File var1);

    Calculator startCalculation(FileInfo var1);

    Deletion startDeletion(File[] var1);

    public interface MsgChDir extends Message<Context> {
        File getPath();
    }

    public interface MsgChOrder extends Message<Context> {
        Order getOrder();
    }

    public interface MsgStarting extends Message<Context> {
        Task getTask();
    }
}
