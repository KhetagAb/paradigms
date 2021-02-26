package queue;

import java.util.Arrays;
import java.util.Objects;

public class ArrayQueue {
    /*
        MODEL:
            [a_1, a_2, ..., a_size]
            size -- размер очереди

        Inv:
            size >= 0
            forall i = 1..size: a_i != null

        Let Imm: forall i = 1..size': a[i] = a'[i]
    */

    private int front = 0, tail = 0;
    private Object[] elements = new Object[1];

    /*
        PRED: e != null
        POST: size = size' + 1 && a[size] = e && Imm
    */
    public void enqueue(final Object element) {
        ensureCapacity();
        elements[tail] = Objects.requireNonNull(element);
        tail = (tail + 1) % elements.length;
    }

    /*
        PRED: e != null
        POST: size = size' + 1 && a[1] = e && forall i = 2..size': a[i + 1] = a'[i]
    */
    public void push(final Object element) {
        Objects.requireNonNull(element);

        ensureCapacity();
        front = (front - 1 + elements.length) % elements.length;
        elements[front] = element;
    }

    private void ensureCapacity() {
        if (tail == front && Objects.nonNull(elements[front])) {
            tail = elements.length;
            elements = Arrays.copyOf(elements, elements.length * 2);
            // :NOTE: Копирование в ручную
            for (int i = 0; i < front; i++) {
                elements[tail] = elements[i];
                elements[i] = null;
                tail++;
            }
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

        return elements[(tail - 1 + elements.length) % elements.length];
    }

    /*
        PRED: size > 0
        POST: R == a[1] && size = size' - 1 && forall i = 1..size: a[i] = a'[i + 1]
    */
    public Object dequeue() {
        assert !isEmpty();

        final Object result = elements[front];
        elements[front] = null;
        front = (front + 1) % elements.length;

        return result;
    }

    /*
        PRED: size > 0
        POST: R == a[n] && size = size' - 1 && forall i = 1..size': a[i] = a'[i]
    */
    public Object remove() {
        assert !isEmpty();

        tail = (tail - 1 + elements.length) % elements.length;
        final Object result = elements[tail];
        elements[tail] = null;

        return result;
    }

    /*
        PRED: true
        POST: R == size && Imm
    */
    public int size() {
        if (front == tail && !isEmpty()) {
            return elements.length;
        } else {
            return (tail - front + elements.length) % elements.length;
        }
    }

    /*
        PRED: true
        POST: R == [size == 0] && Imm
    */
    public boolean isEmpty() {
        return front == tail && !Objects.nonNull(elements[front]);
    }

    /*
        PRED: true
        POST: size == 0
    */
    public void clear() {
        for (int i = front; i < tail; i = (i + 1) % elements.length) {
            elements[i] = null;
        }
        elements = new Object[1];
        front = tail = 0;
    }

    /*
        PRED: true
        POST: R = [a_1, a_2, ..., a_size] && Imm
    */
    public Object[] toArray() {
        final int len = size();
        final Object[] result = new Object[len];

        // :NOTE: Копирование в ручную
        for (int i = 0; i < len; i++) {
            result[i] = elements[(i + front) % elements.length];
        }

        return result;
    }

    /*
        PRED: true
        POST: R = "[a_1, ... , a_size]" && Imm
    */
    public String toStr() {
        // :NOTE: TODO
        final String[] result = Arrays.stream(toArray()).map(Object::toString).toArray(String[]::new);

        return '[' + String.join(", ", result) + ']';
    }
}
