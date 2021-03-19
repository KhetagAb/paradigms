package expression.generic;

public class DoubleCalculator extends GenericCalculator<Double> {
    @Override
    public Double add(Double left, Double right) {
        return left + right;
    }

    @Override
    public Double subtract(Double left, Double right) {
        return left - right;
    }

    @Override
    public Double multiply(Double left, Double right) {
        return left * right;
    }

    @Override
    public Double divide(Double left, Double right) {
        return left / right;
    }

    @Override
    public Double mod(Double left, Double right) {
        return left % right;
    }

    @Override
    public Double negate(Double value) {
        return -value;
    }

    @Override
    public Double zero() {
        return 0.;
    }

    @Override
    public Double valueOf(int value) {
        return (double) value;
    }

    @Override
    public Double valueOf(String value) {
        return Double.valueOf(value);
    }

    @Override
    public int compare(Double o1, Double o2) {
        return o1.compareTo(o2);
    }
}
