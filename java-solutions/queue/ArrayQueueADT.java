package queue;

import java.util.Arrays;
import java.util.Objects;

public class ArrayQueueADT extends CommonArrayQueue {
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
        PRED: true
        POST: R.size == 0 && R новый
    */
    public static ArrayQueueADT create() {
        return new ArrayQueueADT();
    }
    /*
        PRED: e != null && queue != null
        POST: size = size' + 1 && a[size] = e && Imm
    */
    public static void enqueue(final ArrayQueueADT queue, final Object element) {
        assert Objects.nonNull(element) && Objects.nonNull(queue);

        ensureCapacity(queue);
        queue.elements[(queue.front + queue.size) % queue.elements.length] = element;
        queue.size++;
    }

    /*
        PRED: e != null && queue != null
        POST: size = size' + 1 && a[1] = e && forall i = 2..size': a[i + 1] = a'[i]
    */
    public static void push(final ArrayQueueADT queue, final Object element) {
        assert Objects.nonNull(element) && Objects.nonNull(queue);

        ensureCapacity(queue);
        queue.front = (queue.front - 1 + queue.elements.length) % queue.elements.length;
        queue.elements[queue.front] = element;
        queue.size++;
    }

    private static void ensureCapacity(final ArrayQueueADT queue) {
        if (queue.size == queue.elements.length) {
            queue.elements = castToSeries(queue.elements, queue.front, queue.size, queue.elements.length * 2);

            queue.front = 0;
        }
    }

    /*
        PRED: size > 0 && queue != null
        POST: R == a[1] && size = size' && Imm
    */
    public static Object element(final ArrayQueueADT queue) {
        assert Objects.nonNull(queue) && !isEmpty(queue);

        return queue.elements[queue.front];
    }

    /*
        PRED: size > 0 && queue != null
        POST: R == a[n] && size = size' && Imm
    */
    public static Object peek(final ArrayQueueADT queue) {
        assert Objects.nonNull(queue) && !isEmpty(queue);

        return queue.elements[(queue.front + queue.size - 1) % queue.elements.length];
    }

    /*
        PRED: size > 0 && queue != null
        POST: R == a[1] && size = size' - 1 && forall i = 1..size: a[i] = a'[i + 1]
    */
    public static Object dequeue(final ArrayQueueADT queue) {
        assert Objects.nonNull(queue) && !isEmpty(queue);

        final Object result = queue.elements[queue.front];
        queue.elements[queue.front] = null;
        queue.front = (queue.front + 1) % queue.elements.length;
        queue.size--;

        return result;
    }

    /*
        PRED: size > 0 && queue != null
        POST: R == a[n] && size = size' - 1 && && Imm
    */
    public static Object remove(final ArrayQueueADT queue) {
        assert Objects.nonNull(queue) && !isEmpty(queue);

        queue.size--;
        final int tail = (queue.front + queue.size) % queue.elements.length;
        final Object result = queue.elements[tail];
        queue.elements[tail] = null;

        return result;
    }

    /*
        PRED: queue != null
        POST: R == size && size = size' && Imm
    */
    public static int size(final ArrayQueueADT queue) {
        assert Objects.nonNull(queue);

        return queue.size;
    }

    /*
        PRED: queue != null
        POST: R == [size == 0] && size = size' && Imm
    */
    public static boolean isEmpty(final ArrayQueueADT queue) {
        assert Objects.nonNull(queue);

        return queue.size == 0;
    }

    /*
        PRED: queue != null
        POST: size == 0 && size = size' && Imm
    */
    public static void clear(final ArrayQueueADT queue) {
        assert Objects.nonNull(queue);

        Arrays.fill(queue.elements, null);
        queue.elements = new Object[2];
        queue.front = queue.size = 0;
    }

    /*
        PRED: queue != null
        POST: R = [a_1, a_2, ..., a_size] && size = size' && Imm
    */
    public static Object[] toArray(final ArrayQueueADT queue) {
        assert Objects.nonNull(queue);

        return castToSeries(queue.elements, queue.front, queue.size, queue.size);
    }

    /*
        PRED: queue != null
        POST: R = "[a_1, ... , a_size]" && size = size' && Imm
    */
    public static String toStr(final ArrayQueueADT queue) {
        assert Objects.nonNull(queue);

        return Arrays.toString(toArray(queue));
    }
}
