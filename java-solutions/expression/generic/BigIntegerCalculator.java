package expression.generic;

import expression.exceptions.DivideByZeroException;

import java.math.BigInteger;

public class BigIntegerCalculator extends Calculator<BigInteger> {
    @Override
    public BigInteger add(BigInteger left, BigInteger right) {
        return left.add(right);
    }

    @Override
    public BigInteger subtract(BigInteger left, BigInteger right) {
        return left.subtract(right);
    }

    @Override
    public BigInteger multiply(BigInteger left, BigInteger right) {
        return left.multiply(right);
    }

    @Override
    public BigInteger divide(BigInteger left, BigInteger right) {
        if (right.equals(BigInteger.ZERO)) {
            throw new DivideByZeroException(left + " divide 0");
        }
        return left.divide(right);
    }

    @Override
    public BigInteger negate(BigInteger value) {
        return value.negate();
    }

    @Override
    public BigInteger valueOf(int value) {
        return BigInteger.valueOf(value);
    }

    @Override
    public BigInteger valueOf(String value) {
        return new BigInteger(value);
    }
}
