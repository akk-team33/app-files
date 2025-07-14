package net.team33.application.logging;

import java.io.PrintWriter;
import java.util.Date;

public class PlainFormat implements Formatter {

    @Override
    public final void format(final Loggable entry, final PrintWriter out) {
        final Date date = entry.getDate();
        final String type = entry.getLevel().toString();
        final Thread thread = entry.getThread();
        out.println(String.format("[%2$s - thread#%3$d/\"%4$s\" - %1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS,%1$tL]", date, type, thread.getId(), thread.getName()));
        final String text = entry.getText();
        if (text != null) {
            out.println(text);
        }

        final Throwable ex = entry.getException();
        if (ex != null) {
            ex.printStackTrace(out);
        }

    }
}
