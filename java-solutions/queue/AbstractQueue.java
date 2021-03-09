package queue;

import java.util.Arrays;
import java.util.Objects;

public abstract class AbstractQueue implements Queue {
    protected int size = 0;

    protected abstract void enqueueImpl(final Object element);
    protected abstract void dequeueImpl();
    protected abstract void pushImpl(final Object element);
    protected abstract void removeImpl();
    protected abstract void clearImpl();

    @FunctionalInterface
    private interface Removing {
        void act();
    }

    @FunctionalInterface
    private interface Adding {
        void act(final Object element);
    }

    protected Object removing(final Removing func) {
        assert !isEmpty();
        final Object result = element();
        size--;

        func.act();

        return result;
    }

    protected void adding(final Adding func, final Object element) {
        assert Objects.nonNull(element);
        size++;

        func.act(element);
    }

    @Override
    public void enqueue(final Object element) {
        adding(this::enqueueImpl, element);
    }

    @Override
    public void push(final Object element) {
        adding(this::pushImpl, element);
    }

    @Override
    public Object dequeue() {
        return removing(this::dequeueImpl);
    }

    @Override
    public Object remove() {
        return removing(this::removeImpl);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        size = 0;

        clearImpl();
    }

    @Override
    public String toStr() {
        return Arrays.toString(toArray());
    }
}
