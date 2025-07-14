//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.team33.fscalc.task.impl;

import net.team33.fscalc.info.FileInfo;
import net.team33.fscalc.task.ctrl.ControllerBasEx;

public class Calculator extends TaskBase {
    private static final String PREFIX = "Ermittle";
    private FileInfo info = null;

    public Calculator(FileInfo info) {
        this.info = info;
    }

    protected String getProgressPrefix() {
        return "Ermittle";
    }

    protected void run_core() {
        this.info.calculate(new CONTROLLER());
    }

    private class CONTROLLER extends ControllerBasEx {
        private CONTROLLER() {
        }

        protected void setProgress(String subject, double rate) {
            Calculator.this.fireProgress(subject, rate);
        }

        public boolean isQuitting() {
            return Calculator.this.isQuit();
        }
    }
}
