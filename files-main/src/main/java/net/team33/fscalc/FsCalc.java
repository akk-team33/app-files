//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.team33.fscalc;

import net.team33.application.Log;
import net.team33.application.logging.Level;
import net.team33.application.logging.TargetStream;
import net.team33.fscalc.ui.MainFrame;
import net.team33.fscalc.work.Context;
import net.team33.fscalc.work.ContextImpl;
import net.team33.fscalc.work.Order;
import net.team33.swinx.LogTarget;

import javax.swing.*;
import java.io.File;

public class FsCalc implements Runnable {
    private ContextImpl context;

    static {
        Log.add(new TargetStream(System.out, Log.getStdFormat()), new Level[]{Level.DEBUG, Level.INFO});
        Log.add(new TargetStream(System.err, Log.getStdFormat()), new Level[]{Level.WARNING, Level.ERROR, Level.FATAL});
        Log.add(new LogTarget("Fehler Controller - FSCalc", Log.getStdFormat()), new Level[]{Level.WARNING, Level.ERROR, Level.FATAL});
        Log.setDefaultUncaughtExceptionHandler();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new FsCalc(args));
    }

    public FsCalc(String[] args) {
        if (args.length > 0) {
            this.context = new ContextImpl(new File(args[0]), Order.DEFAULT_ASC);
        } else {
            this.context = new ContextImpl(new File("."), Order.DEFAULT_ASC);
        }

    }

    public void run() {
        MainFrame frame = new FRAME();
        frame.setVisible(true);
    }

    private class FRAME extends MainFrame {
        private FRAME() {
        }

        protected Context getContext() {
            return FsCalc.this.context;
        }
    }
}
