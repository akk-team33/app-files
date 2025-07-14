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
    public final void increment(String subject, long divisor) {
        this.increment(subject, new BigInteger(String.valueOf(divisor)));
    }

    private void increment(String subject, BigInteger divisor) {
        BigInteger x = this.divisor.add(this.dividend.multiply(divisor));
        BigInteger y = divisor.multiply(this.divisor);
        BigInteger d = y.gcd(x);
        this.dividend = x.divide(d);
        this.divisor = y.divide(d);
        this.setProgress(subject, this.dividend.doubleValue() / this.divisor.doubleValue());
    }

    protected abstract void setProgress(String var1, double var2);
}
