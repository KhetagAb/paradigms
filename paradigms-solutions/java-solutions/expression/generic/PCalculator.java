package expression.generic;

import expression.exceptions.DivideByZeroException;

public class PCalculator extends GenericCalculator<Integer> {
    final int mod = 1009;
    final int[] reverseMod = new int[1009];

    public PCalculator() {
        reverseMod[1] = 1;
        for (int i = 2; i < 1009; ++i)
            reverseMod[i] = (mod - (mod / i) * reverseMod[mod % i] % mod) % mod;
    }

    @Override
    public Integer add(Integer left, Integer right) {
        return mod(left + right);
    }

    @Override
    public Integer subtract(Integer left, Integer right) {
        return mod(left - right);
    }

    @Override
    public Integer multiply(Integer left, Integer right) {
        return mod(left * right);
    }

    @Override
    public Integer divide(Integer left, Integer right) {
        if (right == 0) {
            throw new DivideByZeroException(left + " divide 0");
        }

        return mod(left * reverseMod[mod(right)]);
    }

    @Override
    public Integer mod(Integer left, Integer right) {
        if (right == 0) {
            throw new DivideByZeroException(left + " divide 0");
        }

        return mod(left % right);
    }

    @Override
    public Integer negate(Integer value) {
        return mod(-value);
    }

    @Override
    public Integer zero() {
        return 0;
    }

    @Override
    public Integer valueOf(int value) {
        return mod(value);
    }

    @Override
    public Integer valueOf(String value) {
        return mod(Integer.valueOf(value));
    }

    @Override
    public int compare(Integer o1, Integer o2) {
        return o1.compareTo(o2);
    }

    protected Integer mod(Integer value) {
        return (mod + value % mod) % mod;
    }
}
