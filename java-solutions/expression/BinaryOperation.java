package expression;

import expression.generic.Calculator;

public abstract class BinaryOperation<T> extends Operation<T> {
    protected final Expression<T> left, right;

    protected abstract T operate(T left, T right);

    protected BinaryOperation(Expression<T> left, Expression<T> right, Calculator<T> calculator) {
        super(calculator);
        this.left = left;
        this.right = right;
    }

    @Override
    public T evaluate(int x, int y, int z) {
        return operate(left.evaluate(x, y, z), right.evaluate(x, y, z));
    }

    @Override
    public String toString() {
        return "(" + left.toString() +
                " " + getSymbol() + " " +
                right.toString() + ")";
    }
}
