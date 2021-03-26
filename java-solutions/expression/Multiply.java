package expression;

import expression.generic.Calculator;

public class Multiply<T> extends BinaryOperation<T> {
    public Multiply(Expression<T> left, Expression<T> right, Calculator<T> calculator) {
        super(left, right, calculator);
    }

    @Override
    protected T operate(T left, T right) {
        return calculator.multiply(left, right);
    }

    @Override
    public String getSymbol() {
        return "*";
    }
}