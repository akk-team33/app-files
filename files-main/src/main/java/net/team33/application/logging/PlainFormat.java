//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.team33.application.logging;

import java.io.PrintWriter;
import java.util.Date;

public class PlainFormat implements Formatter {
    public PlainFormat() {
    }

    public void format(Loggable entry, PrintWriter out) {
        Date date = entry.getDate();
        String type = entry.getLevel().toString();
        Thread thread = entry.getThread();
        out.println(String.format("[%2$s - thread#%3$d/\"%4$s\" - %1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS,%1$tL]", date, type, thread.getId(), thread.getName()));
        String text = entry.getText();
        if (text != null) {
            out.println(text);
        }

        Throwable ex = entry.getException();
        if (ex != null) {
            ex.printStackTrace(out);
        }

    }
}
