package net.team33.fscalc.task.ctrl;

import java.math.BigInteger;

public abstract class ControllerBasEx extends ControllerBase {
    private BigInteger dividend;
    private BigInteger divisor;

    public ControllerBasEx() {
        this.dividend = BigInteger.ZERO;
        this.divisor = BigInteger.ONE;
    }

    @Override
    public final void increment(final String subject, final long divisor) {
        increment(subject, new BigInteger(String.valueOf(divisor)));
    }

    private void increment(final String subject, final BigInteger divisor) {
        final BigInteger x = this.divisor.add(dividend.multiply(divisor));
        final BigInteger y = divisor.multiply(this.divisor);
        final BigInteger d = y.gcd(x);
        this.dividend = x.divide(d);
        this.divisor = y.divide(d);
        setProgress(subject, dividend.doubleValue() / this.divisor.doubleValue());
    }

    protected abstract void setProgress(String var1, double var2);
}
