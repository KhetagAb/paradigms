package expression;

import expression.generic.Calculator;

public class Add<T> extends BinaryOperation<T> {
    public Add(GenericExpression<T> left, GenericExpression<T> right, Calculator<T> calculator) {
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
