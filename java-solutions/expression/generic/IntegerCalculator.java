package expression.generic;

import expression.exceptions.DivideByZeroException;

public class IntegerCalculator extends GenericCalculator<Integer> {
    @Override
    public Integer add(Integer left, Integer right) {
        return left + right;
    }

    @Override
    public Integer subtract(Integer left, Integer right) {
        return left - right;
    }

    @Override
    public Integer multiply(Integer left, Integer right) {
        return left * right;
    }

    @Override
    public Integer divide(Integer left, Integer right) {
        if (right == 0) {
            throw new DivideByZeroException(left + " divide 0");
        }

        return left / right;
    }

    @Override
    public Integer mod(Integer left, Integer right) {
        if (right == 0) {
            throw new DivideByZeroException(left + " divide 0");
        }

        return left % right;
    }

    @Override
    public Integer negate(Integer value) {
        return -value;
    }

    @Override
    public Integer zero() {
        return 0;
    }

    @Override
    public Integer valueOf(int value) {
        return value;
    }

    @Override
    public Integer valueOf(String value) {
        return Integer.valueOf(value);
    }

    @Override
    public int compare(Integer o1, Integer o2) {
        return o1.compareTo(o2);
    }
}
