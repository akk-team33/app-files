package de.team33.files.luna.busyness;

import de.team33.files.luna.context.Icons;
import de.team33.files.luna.context.TableViewConfig;
import de.team33.files.luna.context.UIContext;
import de.team33.files.luna.resources.Ico;
import de.team33.patterns.execution.metis.SimpleAsyncExecutor;
import de.team33.patterns.io.adrastea.FileEntry;
import de.team33.patterns.serving.alpha.Component;
import de.team33.patterns.serving.alpha.Variable;

import javax.swing.*;
import java.nio.file.Path;
import java.util.concurrent.Executor;

public class Backend implements UIContext {

    private static final Icons icons = new IconRecord(Ico.CLSDIR,
                                                      Ico.OPNDIR,
                                                      Ico.FILE,
                                                      Ico.UPDIR);

    private final Executor executor = new SimpleAsyncExecutor();
    private final Variable<Path> cwd = new Component<>(executor, Path.of("."), Backend::normalize);
    private final TableViewConfig tableViewConfig = new TableViewConfigImpl();

    private static Path normalize(final Path path) throws Component.SetException {
        final FileEntry entry = FileEntry.resolved(path);
        if (entry.isDirectory()) {
            return entry.path();
        }
        throw new Component.SetException();
    }

    @Override
    public final Executor executor() {
        return executor;
    }

    @Override
    public final Icons icons() {
        return icons;
    }

    @Override
    public final Variable<Path> cwd() {
        return cwd;
    }

    @Override
    public final TableViewConfig tableViewConfig() {
        return tableViewConfig;
    }

    private record IconRecord(Icon stdFolder,
                              Icon opnFolder,
                              Icon stdFile,
                              Icon upFolder)
            implements Icons {
    }
}
