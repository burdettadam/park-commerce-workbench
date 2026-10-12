package dev.parkcommerce.core;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** USD-only, integer-cent money. No binary floating-point calculation. */
public record Money(long cents) {
    public Money { Failure.require(cents >= 0 && cents <= 2_000_000_000L, "Amount is outside the supported range"); }
    public static Money usd(String value) {
        if (value == null) throw Failure.invalid("A monetary amount is required");
        try {
            return new Money(new BigDecimal(value).setScale(2, RoundingMode.UNNECESSARY).movePointRight(2).longValueExact());
        } catch (ArithmeticException | NumberFormatException e) {
            throw Failure.invalid("Money must have at most two nonzero decimal places");
        }
    }
    public Money times(int quantity) {
        Failure.require(quantity >= 0 && quantity <= 20, "Quantity must be between 0 and 20");
        try { return new Money(Math.multiplyExact(cents, quantity)); }
        catch (ArithmeticException e) { throw Failure.invalid("Monetary overflow"); }
    }
    public Money plus(Money other) { return new Money(Math.addExact(cents, other.cents)); }
    public Money minus(Money other) { return new Money(Math.subtractExact(cents, other.cents)); }
    public Money discount(int basisPoints) {
        Failure.require(basisPoints >= 0 && basisPoints <= 10_000, "Discount must be between 0 and 100 percent");
        // Round each line's discount half-up to one cent, then sum lines.
        return new Money(BigDecimal.valueOf(cents).multiply(BigDecimal.valueOf(basisPoints))
            .divide(BigDecimal.valueOf(10_000), 0, RoundingMode.HALF_UP).longValueExact());
    }
    public String amount() { return BigDecimal.valueOf(cents, 2).toPlainString(); }
    public String currency() { return "USD"; }
}
