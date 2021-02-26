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

    private int size = 0;
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
        PRED: e != null
        POST: size = size' + 1 && a[size] = e && Imm: forall i = 1..size': a[i] = a'[i]
    */
    public static void enqueue(ArrayQueueADT queue, Object element) {
        Objects.requireNonNull(element);

        ensureCapacity(queue);
        queue.elements[queue.tail] = element;
        queue.tail = (queue.tail + 1) % queue.elements.length;
        queue.size++;
    }

    private static void ensureCapacity(ArrayQueueADT queue) {
        if (queue.tail == queue.front && queue.size != 0) {
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
        PRED: size > 0
        POST: R == a[1] && size = size' && Imm
    */
    public static Object element(ArrayQueueADT queue) {
        assert queue.size > 0;

        return queue.elements[queue.front];
    }

    /*
        PRED: size > 0
        POST: R == a[1] && size = size' - 1 && forall i = 1..size: a[i] = a'[i + 1]
    */
    public static Object dequeue(ArrayQueueADT queue) {
        assert queue.size > 0;

        Object result = queue.elements[queue.front];
        queue.elements[queue.front] = null;
        queue.front = (queue.front + 1) % queue.elements.length;
        queue.size--;

        return result;
    }

    /*
        PRED: true
        POST: R == size && Imm
    */
    public static int size(ArrayQueueADT queue) {
        return queue.size;
    }

    /*
        PRED: true
        POST: R == [size == 0] && Imm
    */
    public static boolean isEmpty(ArrayQueueADT queue) {
        return queue.size == 0;
    }

    /*
        PRED: true
        POST: size == 0
    */
    public static void clear(ArrayQueueADT queue) {
        for (int i = queue.front; i < queue.tail; i = (i + 1) % queue.elements.length) {
            queue.elements[i] = null;
        }
        queue.elements = new Object[1];
        queue.size = 0;
        queue.front = queue.tail = 0;
    }
}
