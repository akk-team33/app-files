//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.team33.application.logging;

import net.team33.messaging.Listener;
import net.team33.utils.FileUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class TargetStream implements Listener<Loggable> {
    private PrintWriter out;
    private Formatter fmt;

    public TargetStream(OutputStream out, Formatter fmt) {
        this.out = new PrintWriter(out);
        this.fmt = fmt;
    }

    public TargetStream(String filePath, Formatter fmt) throws FileNotFoundException {
        this((OutputStream)(new FileOutputStream(FileUtil.getAccessible(filePath), false)), fmt);
    }

    public synchronized void pass(Loggable entry) {
        this.fmt.format(entry, this.out);
        this.out.println();
        this.out.flush();
    }
}
