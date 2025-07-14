//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

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

    protected String getProgressPrefix() {
        return "Lösche";
    }

    protected void run_core() {
        Controller ctrl = new CONTROLLER();
        FileService.getInstance().delete(this.paths, ctrl.getSubController((long)this.paths.length));
    }

    private class CONTROLLER extends ControllerBasEx {
        private CONTROLLER() {
        }

        protected void setProgress(String subject, double rate) {
            Deletion.this.fireProgress(subject, rate);
        }

        public boolean isQuitting() {
            return Deletion.this.isQuit();
        }
    }
}
