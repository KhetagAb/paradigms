package queue;

import java.util.Arrays;

public class ArrayQueue extends AbstractQueue {
    private int head = 0;
    private Object[] elements = new Object[2];

    @Override
    protected void enqueueImpl(final Object element) {
        ensureCapacity();
        elements[(head + size - 1) % elements.length] = element;
    }

    @Override
    protected void dequeueImpl() {
        elements[head] = null;
        head = (head + 1) % elements.length;
    }

    private void ensureCapacity() {
        if (size == elements.length) {
            elements = CommonArrayQueue.castToSeries(elements, head, size, elements.length * 2);

            head = 0;
        }
    }

    @Override
    public Object element() {
        return elements[head];
    }

    @Override
    protected void clearImpl() {
        head = 0;

        Arrays.fill(elements, null);
        elements = new Object[2];
    }

    @Override
    protected Queue createQueue() {
        return new ArrayQueue();
    }
}
