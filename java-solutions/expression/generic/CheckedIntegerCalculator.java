package expression.generic;

import expression.exceptions.DivideByZeroException;
import expression.exceptions.ExpressionOverflowException;

public class CheckedIntegerCalculator extends Calculator<Integer> {
    @Override
    public Integer add(Integer left, Integer right) {
        if (left > 0 && Integer.MAX_VALUE - left < right ||
                left < 0 && Integer.MIN_VALUE - left > right) {
            throw new ExpressionOverflowException(left + " add " + right);
        }

        return left + right;
    }

    @Override
    public Integer subtract(Integer left, Integer right) {
        if (right < 0 && Integer.MAX_VALUE + right < left ||
                right > 0 && Integer.MIN_VALUE + right > left) {
            throw new ExpressionOverflowException(left + " subtract " + right);
        }

        return left - right;
    }

    @Override
    public Integer multiply(Integer left, Integer right) {
        if (left > 0 && (right > 0 && Integer.MAX_VALUE / left < right || right < 0 && Integer.MIN_VALUE / left > right) ||
                left < 0 && (right < 0 && Integer.MAX_VALUE / right > left || right > 0 && Integer.MIN_VALUE / right > left)) {
            throw new ExpressionOverflowException(left + " multiply " + right);
        }

        return left * right;
    }

    @Override
    public Integer divide(Integer left, Integer right) {
        if (right == 0) {
            throw new DivideByZeroException(left + " divide 0");
        } else if (left == Integer.MIN_VALUE && right == -1) {
            throw new ExpressionOverflowException(left + " divide " + right);
        }

        return left / right;
    }

    @Override
    public Integer negate(Integer value) {
        if (value == Integer.MIN_VALUE) {
            throw new ExpressionOverflowException("negate " + value);
        }

        return -value;
    }

    @Override
    public Integer valueOf(int value) {
        return value;
    }

    @Override
    public Integer valueOf(String value) {
        return Integer.valueOf(value);
    }
}
