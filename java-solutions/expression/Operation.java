package expression;

import expression.generic.Calculator;

public abstract class Operation<T extends Number> implements Expression<T> {
    protected final Calculator<T> calculator;

    protected Operation(Calculator<T> calculator) {
        this.calculator = calculator;
    }

    public abstract String getSymbol();
}