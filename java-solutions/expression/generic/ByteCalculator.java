package expression.generic;

import expression.exceptions.DivideByZeroException;

public class ByteCalculator extends GenericCalculator<Byte> {
    @Override
    public Byte add(Byte left, Byte right) {
        return (byte) (left + right);
    }

    @Override
    public Byte subtract(Byte left, Byte right) {
        return (byte) (left - right);
    }

    @Override
    public Byte multiply(Byte left, Byte right) {
        return (byte) (left * right);
    }

    @Override
    public Byte divide(Byte left, Byte right) {
        if (right == 0) {
            throw new DivideByZeroException(left + " divide 0");
        }

        return (byte) (left / right);
    }

    @Override
    public Byte mod(Byte left, Byte right) {
        if (right == 0) {
            throw new DivideByZeroException(left + " divide 0");
        }

        return (byte) (left % right);
    }

    @Override
    public Byte negate(Byte value) {
        return (byte) -value;
    }

    @Override
    public Byte zero() {
        return 0;
    }

    @Override
    public Byte valueOf(int value) {
        return (byte) value;
    }

    @Override
    public Byte valueOf(String value) {
        return Byte.valueOf(value);
    }

    @Override
    public int compare(Byte o1, Byte o2) {
        return o1.compareTo(o2);
    }
}
