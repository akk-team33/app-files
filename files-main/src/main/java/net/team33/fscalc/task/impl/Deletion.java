package net.team33.fscalc.task.impl;

import net.team33.fscalc.info.FileService;
import net.team33.fscalc.task.Controller;
import net.team33.fscalc.task.ctrl.ControllerBasEx;

import java.io.File;

public class Deletion extends TaskBase {
    private static final String PREFIX = "Lösche";
    private File[] paths = null;

    public Deletion(File[] paths) {
        this.paths = paths;
    }

    @Override
    protected final String getProgressPrefix() {
        return "Lösche";
    }

    @Override
    protected final void run_core() {
        Controller ctrl = new CONTROLLER();
        FileService.getInstance().delete(this.paths, ctrl.getSubController((long)this.paths.length));
    }

    private class CONTROLLER extends ControllerBasEx {

        @Override
        protected final void setProgress(String subject, double rate) {
            Deletion.this.fireProgress(subject, rate);
        }

        @Override
        public final boolean isQuitting() {
            return Deletion.this.isQuit();
        }
    }
}
