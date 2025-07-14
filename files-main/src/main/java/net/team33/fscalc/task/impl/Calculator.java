package net.team33.fscalc.task.impl;

import net.team33.fscalc.info.FileInfo;
import net.team33.fscalc.task.ctrl.ControllerBasEx;

public class Calculator extends TaskBase {
    private static final String PREFIX = "Ermittle";
    private FileInfo info = null;

    public Calculator(final FileInfo info) {
        this.info = info;
    }

    @Override
    protected final String getProgressPrefix() {
        return "Ermittle";
    }

    @Override
    protected final void run_core() {
        info.calculate(new CONTROLLER());
    }

    private class CONTROLLER extends ControllerBasEx {

        @Override
        protected final void setProgress(final String subject, final double rate) {
            fireProgress(subject, rate);
        }

        @Override
        public final boolean isQuitting() {
            return isQuit();
        }
    }
}
