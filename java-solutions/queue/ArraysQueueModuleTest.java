package queue;

public class ArraysQueueModuleTest {
    public static void main(String[] args) {
        fill(0, 10);
        dump();

        fill(0, 5);
        firstAndSize();
        clear();

        fill(5, 5);
        firstAndSize();
    }

    private static void fill(int from, int to) {
        for (int i = from; i <= to; i++) {
            ArrayQueueModule.enqueue(i);
        }
    }

    private static void dump() {
        while (!ArrayQueueModule.isEmpty()) {
            System.out.println(ArrayQueueModule.dequeue());
        }
    }

    private static void firstAndSize() {
        System.out.printf("Size %d:     %o...%n", ArrayQueueModule.size(), ArrayQueueModule.element());
    }

    private static void clear() {
        ArrayQueueModule.clear();
    }
}