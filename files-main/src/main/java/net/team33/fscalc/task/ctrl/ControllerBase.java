package net.team33.fscalc.task.ctrl;

import net.team33.fscalc.task.Controller;

public abstract class ControllerBase implements Controller {

    @Override
    public final Controller getSubController(final long preDivisor) {
        return new SUB_CONTROLLER(this, preDivisor);
    }

    private static class SUB_CONTROLLER extends ControllerBase {
        ControllerBase parent;
        long preDiv;

        private SUB_CONTROLLER(final ControllerBase parent, final long preDiv) {
            this.parent = parent;
            this.preDiv = preDiv;
        }

        @Override
        public final void increment(final String subject, final long divisor) {
            parent.increment(subject, divisor * preDiv);
        }

        @Override
        public final boolean isQuitting() {
            return parent.isQuitting();
        }
    }
}
