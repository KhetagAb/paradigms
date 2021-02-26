package queue;

import java.util.Arrays;
import java.util.Objects;

public class ArrayQueueADT {
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
        PRED: true
        POST: R.size == 0 && R новый
    */
    public static ArrayQueueADT create() {
        return new ArrayQueueADT();
    }

    /*
        PRED: e != null && queue != null
        POST: size = size' + 1 && a[size] = e && Imm: forall i = 1..size': a[i] = a'[i]
    */
    public static void enqueue(ArrayQueueADT queue, Object element) {
        Objects.requireNonNull(element);
        Objects.requireNonNull(queue);

        ensureCapacity(queue);
        queue.elements[queue.tail] = element;
        queue.tail = (queue.tail + 1) % queue.elements.length;
    }

    /*
        PRED: e != null && queue != null
        POST: size = size' + 1 && a[1] = e && Imm: forall i = 2..size': a[i + 1] = a'[i]
    */
    public static void push(ArrayQueueADT queue, Object element) {
        Objects.requireNonNull(element);
        Objects.requireNonNull(queue);

        ensureCapacity(queue);
        queue.front = (queue.front - 1 + queue.elements.length) % queue.elements.length;
        queue.elements[queue.front] = element;
    }

    private static void ensureCapacity(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);

        if (queue.tail == queue.front && Objects.nonNull(queue.elements[queue.front])) {
            queue.tail = queue.elements.length;
            queue.elements = Arrays.copyOf(queue.elements, queue.elements.length * 2);
            for (int i = 0; i < queue.front; i++) {
                queue.elements[queue.tail] = queue.elements[i];
                queue.elements[i] = null;
                queue.tail++;
            }
        }
    }

    /*
        PRED: size > 0 && queue != null
        POST: R == a[1] && size = size' && Imm
    */
    public static Object element(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);
        assert !isEmpty(queue);

        return queue.elements[queue.front];
    }

    /*
        PRED: size > 0 && queue != null
        POST: R == a[n] && size = size' && Imm
    */
    public static Object peek(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);
        assert !isEmpty(queue);

        return queue.elements[(queue.tail - 1 + queue.elements.length) % queue.elements.length];
    }


    /*
        PRED: size > 0 && queue != null
        POST: R == a[1] && size = size' - 1 && forall i = 1..size: a[i] = a'[i + 1]
    */
    public static Object dequeue(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);
        assert !isEmpty(queue);

        Object result = queue.elements[queue.front];
        queue.elements[queue.front] = null;
        queue.front = (queue.front + 1) % queue.elements.length;

        return result;
    }

    /*
        PRED: size > 0 && queue != null
        POST: R == a[n] && size = size' - 1 && forall i = 1..size': a[i] = a'[i]
    */
    public static Object remove(ArrayQueueADT queue) {
        assert !isEmpty(queue);

        queue.tail = (queue.tail - 1 + queue.elements.length) % queue.elements.length;
        Object result = queue.elements[queue.tail];
        queue.elements[queue.tail] = null;

        return result;
    }

    /*
        PRED: true && queue != null
        POST: R == size && Imm
    */
    public static int size(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);

        if (queue.front == queue.tail && !isEmpty(queue)) {
            return queue.elements.length;
        } else {
            return (queue.tail - queue.front + queue.elements.length) % queue.elements.length;
        }
    }

    /*
        PRED: queue != null
        POST: R == [size == 0] && Imm
    */
    public static boolean isEmpty(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);

        return queue.front == queue.tail && !Objects.nonNull(queue.elements[queue.front]);
    }

    /*
        PRED: queue != null
        POST: size == 0
    */
    public static void clear(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);

        for (int i = queue.front; i < queue.tail; i = (i + 1) % queue.elements.length) {
            queue.elements[i] = null;
        }
        queue.elements = new Object[1];
        queue.front = queue.tail = 0;
    }

    /*
        PRED: queue != null
        POST: R = [a_1, a_2, ..., a_size]
    */
    public static Object[] toArray(ArrayQueueADT queue) {
        int len = size(queue);
        Object[] result = new Object[len];

        for (int i = 0; i < len; i++) {
            result[i] = queue.elements[(i + queue.front) % queue.elements.length];
        }

        return result;
    }

    /*
        PRED: queue != null
        POST: R = "[a_1, ... , a_size]"
    */
    public static String toStr(ArrayQueueADT queue) {
        String[] result = Arrays.stream(toArray(queue)).map(Object::toString).toArray(String[]::new);

        return '[' + String.join(", ", result) + ']';
    }
}
