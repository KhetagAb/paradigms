package queue;

import java.util.Arrays;
import java.util.Objects;

public class ArrayQueue {
    /*
        MODEL:
            [a_1, a_2, ..., a_size]
            size -- размер очереди
            first -- первый элемент очереди

        Inv:
            size >= 0
            forall i = 1..size: a_i != null
            first = a[1]
    */

    private int front = 0, tail = 0;
    private Object[] elements = new Object[1];

    /*
        PRED: e != null
        POST: size = size' + 1 && a[size] = e && Imm: forall i = 1..size': a[i] = a'[i]
    */
    public void enqueue(Object element) {
        Objects.requireNonNull(element);

        ensureCapacity();
        elements[tail] = element;
        tail = (tail + 1) % elements.length;
    }

    private void ensureCapacity() {
        if (tail == front && elements[front] != null) {
            tail = elements.length;
            elements = Arrays.copyOf(elements, elements.length * 2);
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
        POST: R == a[1] && size = size' - 1 && forall i = 1..size: a[i] = a'[i + 1]
    */
    public Object dequeue() {
        assert !isEmpty();

        Object result = elements[front];
        elements[front] = null;
        front = (front + 1) % elements.length;

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
        return front == tail && elements[front] == null;
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
}
