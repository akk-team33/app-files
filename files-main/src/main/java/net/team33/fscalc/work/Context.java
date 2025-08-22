package net.team33.fscalc.work;

import de.team33.files.ui.FileTable;
import de.team33.files.ui.FileTree;
import de.team33.patterns.serving.alpha.Retrievable;
import de.team33.patterns.serving.alpha.Variable;
import net.team33.fscalc.info.FileInfo;
import net.team33.fscalc.task.Task;
import net.team33.fscalc.task.impl.Calculator;
import net.team33.fscalc.task.impl.Deletion;
import net.team33.messaging.Message;
import net.team33.messaging.multiplex.Originator;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@SuppressWarnings({"ClassNameSameAsAncestorName", "InterfaceWithOnlyOneDirectInheritor"})
public interface Context extends Originator<Message<Context>>, FileTree.Context, FileTable.Context {

    @Override
    Icons icons();

    @SuppressWarnings("AbstractMethodOverridesAbstractMethod")
    @Override
    Variable<Path> cwd();

    Variable<Order> order();

    @SuppressWarnings("unused")
    Retrievable<List<Task>> tasks();

    @SuppressWarnings("UnusedReturnValue")
    Calculator startCalculation(File file);

    Calculator startCalculation(FileInfo info);

    @SuppressWarnings("UnusedReturnValue")
    Deletion startDeletion(File[] files);

    interface MsgStarting extends Message<Context> {
        Task getTask();
    }

    interface Icons extends FileTree.Icons, FileTable.Icons {
    }
}
