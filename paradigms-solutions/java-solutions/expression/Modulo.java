package expression;

import expression.generic.Calculator;

public class Modulo<T> extends BinaryOperation<T> {
    public Modulo(GenericExpression<T> left, GenericExpression<T> right, Calculator<T> calculator) {
        super(left, right, calculator);
    }

    @Override
    public String getSymbol() {
        return "mod";
    }

    @Override
    protected T operate(T left, T right) {
        return calculator.mod(left, right);
    }
}
