package de.team33.files.main;

import de.team33.files.ui.FilesFrame;
import net.team33.fscalc.work.ContextImpl;
import net.team33.fscalc.work.Order;

import javax.swing.*;
import java.io.File;

public class Files implements Runnable {

    private final ContextImpl context;

    public Files(final String[] args) {
        if (0 < args.length) {
            this.context = new ContextImpl(new File(args[0]), Order.DEFAULT_ASC);
        } else {
            this.context = new ContextImpl(new File("."), Order.DEFAULT_ASC);
        }
    }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Files(args));
    }

    @Override
    public final void run() {
        FilesFrame.show(context);
    }
}
