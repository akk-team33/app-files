package net.team33.application.logging;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class TargetStream implements Consumer<Loggable> {
    private final PrintWriter out;
    private final Formatter fmt;

    public TargetStream(final OutputStream out, final Formatter fmt) {
        this.out = new PrintWriter(out, false, StandardCharsets.UTF_8);
        this.fmt = fmt;
    }

    @Override
    public final synchronized void accept(final Loggable entry) {
        fmt.format(entry, out);
        out.println();
        out.flush();
    }
}
