package expression;

import expression.generic.Calculator;

public abstract class UnaryOperation<T extends Number> extends Operation<T> {
    protected final Expression<T> expression;
    protected abstract T operate(T value);

    protected UnaryOperation(Expression<T> expression, Calculator<T> calculator) {
        super(calculator);
        this.expression = expression;
    }

    @Override
    public T evaluate(int x, int y, int z) {
        return operate(expression.evaluate(x, y, z));
    }

    @Override
    public String toString() {
        return "(" + getSymbol() + " " + expression.toString() + ")";
    }
}
