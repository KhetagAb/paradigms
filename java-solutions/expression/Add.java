package expression;

import expression.generic.Calculator;

public class Add<T extends Number> extends BinaryOperation<T> {
    public Add(Expression<T> left, Expression<T> right, Calculator<T> calculator) {
        super(left, right, calculator);
    }

    @Override
    public String getSymbol() {
        return "+";
    }

    @Override
    protected T operate(T left, T right) {
        return calculator.add(left, right);
    }
}
