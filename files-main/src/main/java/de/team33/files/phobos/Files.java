package de.team33.files.phobos;

import de.team33.files.phobos.ui.Frame;
import de.team33.patterns.execution.metis.SimpleAsyncExecutor;
import de.team33.patterns.serving.alpha.Component;
import de.team33.patterns.serving.alpha.Variable;
import de.team33.sphinx.lambda.SwingApp;

import javax.swing.*;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.function.UnaryOperator;

public class Files extends SwingApp {

    private static final UnaryOperator<Path> NORMAL_PATH = path -> path.toAbsolutePath().normalize();
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

        private final Variable<Path> cwd = new Component<>(executor, NORMAL_PATH, Path.of("."));

        @Override
        public final Variable<Path> cwd() {
            return cwd;
        }
    }
}