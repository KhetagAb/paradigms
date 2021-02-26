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

    /*
        PRED: e != null
        POST: size = size' + 1 && a[1] = e && Imm: forall i = 2..size': a[i + 1] = a'[i]
    */
    public static void push(Object element) {
        Objects.requireNonNull(element);

        ensureCapacity();
        front = (front - 1 + elements.length) % elements.length;
        elements[front] = element;
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
        POST: R == a[n] && size = size' && Imm
    */
    public static Object peek() {
        assert !isEmpty();

        return elements[(tail - 1 + elements.length) % elements.length];
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
        PRED: size > 0
        POST: R == a[n] && size = size' - 1 && forall i = 1..size': a[i] = a'[i]
    */
    public static Object remove() {
        assert !isEmpty();

        tail = (tail - 1 + elements.length) % elements.length;
        Object result = elements[tail];
        elements[tail] = null;

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

    /*
        PRED: true
        POST: R = [a_1, a_2, ..., a_size]
    */
    public static Object[] toArray() {
        int len = size();
        Object[] result = new Object[len];

        for (int i = 0; i < len; i++) {
            result[i] = elements[(i + front) % elements.length];
        }

        return result;
    }

    /*
        PRED: true
        POST: R = "[ a_1 , ... , a_size ]"
    */
    public static String toStr() {
        String[] result = Arrays.stream(toArray()).map(Object::toString).toArray(String[]::new);

        return '[' + String.join(", ", result) + ']';
    }
}
