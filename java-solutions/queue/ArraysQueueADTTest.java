package queue;

public class ArraysQueueADTTest {
    public static void main(String[] args) {
        ArrayQueueADT queue = ArrayQueueADT.create();

        fill(queue, 0, 10);
        dump(queue);

        fill(queue, 0, 5);
        firstAndSize(queue);
        clear(queue);

        fill(queue, 5, 5);
        firstAndSize(queue);
    }

    private static void fill(ArrayQueueADT queue, int from, int to) {
        for (int i = from; i <= to; i++) {
            ArrayQueueADT.enqueue(queue, i);
        }
    }

    private static void dump(ArrayQueueADT queue) {
        while (!ArrayQueueADT.isEmpty(queue)) {
            System.out.println(ArrayQueueADT.dequeue(queue));
        }
    }

    private static void firstAndSize(ArrayQueueADT queue) {
        System.out.printf("Size %d:     %o...%n", ArrayQueueADT.size(queue), ArrayQueueADT.element(queue));
    }

    private static void clear(ArrayQueueADT queue) {
        ArrayQueueADT.clear(queue);
    }
}
