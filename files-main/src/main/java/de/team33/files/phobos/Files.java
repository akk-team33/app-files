package de.team33.files.phobos;

import de.team33.files.phobos.model.History;
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

        private final Component<Path> cwd;
        private final Component<History> history;

        private Context() {
            cwd = new Component<>(executor, Path.of("."), Context::validPath);
            history = new Component<>(executor, new History(cwd.get()));
            cwd.subscribe(this::setHistory);
            history.subscribe(this::getHistory);
        }

        private static Path validPath(final Path path) throws Component.SetException {
            final FileEntry entry = FileEntry.of(path);
            if (entry.isDirectory()) {
                return entry.path();
            } else {
                throw new Component.SetException();
            }
        }

        private void getHistory(final History history) {
            cwd.set(history.path());
        }

        private void setHistory(final Path path) {
            history.set(history.get().setCurrent(path));
        }

        @Override
        public final Variable<Path> cwd() {
            return cwd;
        }

        @Override
        public final Variable<History> history() {
            return history;
        }
    }
}