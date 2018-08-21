package me.exrates.model.util;

import java.math.BigInteger;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Maks on 28.04.2018.
 */
public class AtomicBigInteger {
    private final AtomicReference<BigInteger> bigInteger;

    public AtomicBigInteger(final BigInteger bigInteger) {
        this.bigInteger = new AtomicReference<>(Objects.requireNonNull(bigInteger));
    }

    // Method references left out for demonstration purposes
    public BigInteger incrementAndGet() {
        return bigInteger.accumulateAndGet(BigInteger.ONE, (previous, x) -> previous.add(x));
    }

    public BigInteger getAndIncrement() {
        return bigInteger.getAndAccumulate(BigInteger.ONE, (previous, x) -> previous.add(x));
    }

    public BigInteger get() {
        return bigInteger.get();
    }
}
