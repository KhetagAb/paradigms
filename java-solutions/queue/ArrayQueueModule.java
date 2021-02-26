package queue;

import java.util.Arrays;
import java.util.Objects;

public class ArrayQueueModule {
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

    private static int front = 0, tail = 0;
    private static Object[] elements = new Object[1];

    /*
        PRED: e != null
        POST: size = size' + 1 && a[size] = e && Imm: forall i = 1..size': a[i] = a'[i]
    */
    public static void enqueue(Object element) {
        Objects.requireNonNull(element);

        ensureCapacity();
        elements[tail] = element;
        tail = (tail + 1) % elements.length;
    }

    private static void ensureCapacity() {
        if (tail == front && Objects.nonNull(elements[front])) {
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
    public static Object element() {
        assert !isEmpty();

        return elements[front];
    }

    /*
        PRED: size > 0
        POST: R == a[1] && size = size' - 1 && forall i = 1..size: a[i] = a'[i + 1]
    */
    public static Object dequeue() {
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
    public static int size() {
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
    public static boolean isEmpty() {
        return front == tail && !Objects.nonNull(elements[front]);
    }

    /*
        PRED: true
        POST: size == 0
    */
    public static void clear() {
        for (int i = front; i < tail; i = (i + 1) % elements.length) {
            elements[i] = null;
        }
        elements = new Object[1];
        front = tail = 0;
    }
}
