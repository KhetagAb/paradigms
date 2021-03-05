package queue;

import java.util.Arrays;
import java.util.Objects;

public class ArrayQueue extends CommonArrayQueue {
    /*
        MODEL:
            [a_1, a_2, ..., a_size]
            size -- размер очереди

        Inv:
            size >= 0
            forall i = 1..size: a_i != null

        Let Imm: forall i = 1..size': a[i] = a'[i]
    */

    private int front = 0, size = 0;
    private Object[] elements = new Object[2];

    /*
        PRED: e != null
        POST: size = size' + 1 && a[size] = e && Imm
    */
    public void enqueue(Object element) {
        assert Objects.nonNull(element);

        ensureCapacity();
        elements[(front + size) % elements.length] = element;
        size++;
    }

    /*
        PRED: e != null
        POST: size = size' + 1 && a[1] = e && forall i = 2..size': a[i + 1] = a'[i]
    */
    public void push(Object element) {
        assert Objects.nonNull(element);

        ensureCapacity();
        front = (front - 1 + elements.length) % elements.length;
        elements[front] = element;
        size++;
    }

    private void ensureCapacity() {
        if (size == elements.length) {
            elements = castToSeries(elements, front, size, elements.length * 2);

            front = 0;
        }
    }

    /*
        PRED: size > 0
        POST: R == a[1] && size = size' && Imm
    */
    public Object element() {
        assert !isEmpty();

        return elements[front];
    }

    /*
        PRED: size > 0
        POST: R == a[n] && size = size' && Imm
    */
    public Object peek() {
        assert !isEmpty();

        return elements[(front + size - 1) % elements.length];
    }

    /*
        PRED: size > 0
        POST: R == a[1] && size = size' - 1 && forall i = 1..size: a[i] = a'[i + 1]
    */
    public Object dequeue() {
        assert !isEmpty();

        Object result = elements[front];
        elements[front] = null;
        front = (front + 1) % elements.length;
        size--;

        return result;
    }

    /*
        PRED: size > 0
        POST: R == a[n] && size = size' - 1 && Imm
    */
    public Object remove() {
        assert !isEmpty();

        size--;
        int tail = (front + size) % elements.length;
        Object result = elements[tail];
        elements[tail] = null;

        return result;
    }

    /*
        PRED: true
        POST: R == size && size = size' && Imm
    */
    public int size() {
        return size;
    }

    /*
        PRED: true
        POST: R == [size == 0] && size = size' && Imm
    */
    public boolean isEmpty() {
        return size == 0;
    }

    /*
        PRED: true
        POST: size == 0 && size = size' && Imm
    */
    public void clear() {
        Arrays.fill(elements, null);
        elements = new Object[2];
        front = size = 0;
    }

    /*
        PRED: true
        POST: R = [a_1, a_2, ..., a_size] && size = size' && Imm
    */
    public Object[] toArray() {
        return castToSeries(elements, front, size, size);
    }

    /*
        PRED: true
        POST: R = "[a_1, ... , a_size]" && size = size' && Imm
    */
    public String toStr() {
        return Arrays.toString(toArray());
    }
}
