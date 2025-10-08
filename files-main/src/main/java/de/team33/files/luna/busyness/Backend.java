package de.team33.files.luna.busyness;

import de.team33.patterns.execution.metis.SimpleAsyncExecutor;
import de.team33.patterns.serving.alpha.Component;
import de.team33.patterns.serving.alpha.Variable;

import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.function.UnaryOperator;

public class Backend {

    private static final System.Logger LOGGER = System.getLogger(Backend.class.getCanonicalName());
    private static final UnaryOperator<Path> NORMALIZER = path -> path.toAbsolutePath().normalize();

    private final Executor executor = new SimpleAsyncExecutor();
    private final Component<Path> cwd = new Component<>(executor, NORMALIZER, Path.of("."));

    public final Executor executor() {
        return executor;
    }

    public final Variable<Path> cwd() {
        return cwd;
    }
}
