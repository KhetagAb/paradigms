package queue;

import java.util.Arrays;
import java.util.Objects;

public class ArrayQueueModule extends CommonArrayQueue {
    /*
        MODEL:
            [a_1, a_2, ..., a_size]
            size -- размер очереди

        Inv:
            size >= 0
            forall i = 1..size: a_i != null

        Let Imm: forall i = 1..size': a[i] = a'[i]
    */

    private static int head = 0, size = 0;
    private static Object[] elements = new Object[2];

    /*
        PRED: e != null
        POST: size = size' + 1 && a[size] = e && Imm
    */
    public static void enqueue(Object element) {
        assert Objects.nonNull(element);

        ensureCapacity();
        elements[(head + size) % elements.length] = element;
        size++;
    }

    /*
        PRED: e != null
        POST: size = size' + 1 && a[1] = e && forall i = 2..size': a[i + 1] = a'[i]
    */
    public static void push(Object element) {
        assert Objects.nonNull(element);

        ensureCapacity();
        head = (head - 1 + elements.length) % elements.length;
        elements[head] = element;
        size++;
    }

    private static void ensureCapacity() {
        if (size == elements.length) {
            elements = castToSeries(elements, head, size, elements.length * 2);

            head = 0;
        }
    }

    /*
        PRED: size > 0
        POST: R == a[1] && size = size' && Imm
    */
    public static Object element() {
        assert !isEmpty();

        return elements[head];
    }

    /*
        PRED: size > 0
        POST: R == a[n] && size = size' && Imm
    */
    public static Object peek() {
        assert !isEmpty();

        return elements[(head + size - 1) % elements.length];
    }

    /*
        PRED: size > 0
        POST: R == a[1] && size = size' - 1 && Imm
    */
    public static Object dequeue() {
        assert !isEmpty();

        Object result = elements[head];
        elements[head] = null;
        head = (head + 1) % elements.length;
        size--;

        return result;
    }

    /*
        PRED: size > 0
        POST: R == a[n] && size = size' - 1 && forall i = 1..size': a[i] = a'[i]
    */
    public static Object remove() {
        assert !isEmpty();

        size--;
        int tail = (head + size) % elements.length;
        Object result = elements[tail];
        elements[tail] = null;

        return result;
    }

    /*
        PRED: true
        POST: R == size && size = size' && Imm
    */
    public static int size() {
        return size;
    }

    /*
        PRED: true
        POST: R == [size == 0] && size = size' && Imm
    */
    public static boolean isEmpty() {
        return size == 0;
    }

    /*
        PRED: true
        POST: size == 0 && size = size' && Imm
    */
    public static void clear() {
        Arrays.fill(elements, null);
        elements = new Object[2];
        head = size = 0;
    }

    /*
        PRED: true
        POST: R = [a_1, a_2, ..., a_size] && size = size' && Imm
    */
    public static Object[] toArray() {
        return castToSeries(elements, head, size, size);
    }

    /*
        PRED: true
        POST: R = "[a_1, ... , a_size]" && size = size' && Imm
    */
    public static String toStr() {
        return Arrays.toString(toArray());
    }
}
