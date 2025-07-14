package net.team33.fscalc.task.ctrl;

import net.team33.fscalc.task.Controller;

public abstract class ControllerBase implements Controller {

    @Override
    public final Controller getSubController(long preDivisor) {
        return new SUB_CONTROLLER(this, preDivisor);
    }

    private static class SUB_CONTROLLER extends ControllerBase {
        ControllerBase parent;
        long preDiv;

        private SUB_CONTROLLER(ControllerBase parent, long preDiv) {
            this.parent = parent;
            this.preDiv = preDiv;
        }

        @Override
        public final void increment(String subject, long divisor) {
            this.parent.increment(subject, divisor * this.preDiv);
        }

        @Override
        public final boolean isQuitting() {
            return this.parent.isQuitting();
        }
    }
}
