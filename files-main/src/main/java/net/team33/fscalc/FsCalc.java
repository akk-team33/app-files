package net.team33.fscalc;

import net.team33.application.Log;
import net.team33.application.logging.Level;
import net.team33.application.logging.TargetStream;
import net.team33.fscalc.ui.MainFrame;
import net.team33.fscalc.work.ContextImpl;
import net.team33.fscalc.work.Order;
import net.team33.swinx.LogTarget;

import javax.swing.*;
import java.io.File;

@SuppressWarnings({"UseOfSystemOutOrSystemErr", "HardCodedStringLiteral", "ClassNamePrefixedWithPackageName"})
public final class FsCalc implements Runnable {
    private final ContextImpl context;

    static {
        Log.add(new TargetStream(System.out, Log.getStdFormat()), Level.DEBUG, Level.INFO);
        Log.add(new TargetStream(System.err, Log.getStdFormat()), Level.WARNING, Level.ERROR, Level.FATAL);
        Log.add(new LogTarget("Fehler Controller - FSCalc", Log.getStdFormat()), Level.WARNING, Level.ERROR, Level.FATAL);
        Log.setDefaultUncaughtExceptionHandler();
    }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new FsCalc(args));
    }

    private FsCalc(final String[] args) {
        if (0 < args.length) {
            this.context = new ContextImpl(new File(args[0]), Order.DEFAULT_ASC);
        } else {
            this.context = new ContextImpl(new File("."), Order.DEFAULT_ASC);
        }

    }

    @Override
    public final void run() {
        new MainFrame(context).setVisible(true);
    }
}
