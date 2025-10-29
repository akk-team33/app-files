package de.team33.files.phobos;

import de.team33.files.phobos.ui.Frame;
import de.team33.patterns.execution.metis.SimpleAsyncExecutor;
import de.team33.patterns.io.delta.FileEntry;
import de.team33.patterns.serving.alpha.Component;
import de.team33.patterns.serving.alpha.Variable;
import de.team33.sphinx.lambda.SwingApp;

import javax.swing.*;
import java.nio.file.Path;
import java.util.concurrent.Executor;

public class Files extends SwingApp {

    private final Executor executor = new SimpleAsyncExecutor();

    public static void main(final String[] args) {
        start(new Files());
    }

    @Override
    protected final JFrame newFrame() {
        return Frame.by(new Context()).ui();
    }

    @SuppressWarnings("ClassNameSameAsAncestorName")
    private class Context implements Frame.Context {

        private Path validPath(final Path path) {
            final FileEntry entry = FileEntry.of(path);
            return entry.isDirectory() ? entry.path() : cwd.get();
        }        private final Variable<Path> cwd = new Component<>(executor, this::validPath, Path.of("."));



        @Override
        public final Variable<Path> cwd() {
            return cwd;
        }
    }
}