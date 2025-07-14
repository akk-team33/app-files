package net.team33.application.logging;

import java.io.PrintWriter;

public interface Formatter {
    void format(Loggable var1, PrintWriter var2);
}
