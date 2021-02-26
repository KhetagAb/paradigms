package queue;

public class ArraysQueueTest {
    public static void main(String[] args) {
        ArrayQueue queue = new ArrayQueue();

        fill(queue, 0, 10);
        dump(queue);

        fill(queue, 0, 5);
        firstAndSize(queue);
        clear(queue);

        fill(queue, 5, 5);
        firstAndSize(queue);
    }

    private static void fill(ArrayQueue queue, int from, int to) {
        for (int i = from; i <= to; i++) {
            queue.enqueue(i);
        }
    }

    private static void dump(ArrayQueue queue) {
        while (!queue.isEmpty()) {
            System.out.println(queue.dequeue());
        }
    }

    private static void firstAndSize(ArrayQueue queue) {
        System.out.printf("Size %d:     %o...%n", queue.size(), queue.element());
    }

    private static void clear(ArrayQueue queue) {
        queue.clear();
    }
}
