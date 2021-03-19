package expression.generic;

public class FloatCalculator extends GenericCalculator<Float> {
    @Override
    public Float add(Float left, Float right) {
        return left + right;
    }

    @Override
    public Float subtract(Float left, Float right) {
        return left - right;
    }

    @Override
    public Float multiply(Float left, Float right) {
        return left * right;
    }

    @Override
    public Float divide(Float left, Float right) {
        return left / right;
    }

    @Override
    public Float mod(Float left, Float right) {
        return left % right;
    }

    @Override
    public Float negate(Float value) {
        return -value;
    }

    @Override
    public Float zero() {
        return 0f;
    }

    @Override
    public Float valueOf(int value) {
        return (float) value;
    }

    @Override
    public Float valueOf(String value) {
        return Float.valueOf(value);
    }

    @Override
    public int compare(Float o1, Float o2) {
        return o1.compareTo(o2);
    }
}
