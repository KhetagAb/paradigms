package queue;

public abstract class AbstractQueue implements Queue {
    protected int size = 0;

    protected abstract void enqueueImpl(final Object element);
    protected abstract void dequeueImpl();
    protected abstract void clearImpl();
    protected abstract Queue createQueue();

    @Override
    public void enqueue(final Object element) {
        size++;

        enqueueImpl(element);
    }

    @Override
    public Object dequeue() {
        final Object result = element();
        size--;

        dequeueImpl();

        return result;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    private Queue nth(int n, boolean drop, boolean inPlace) {
        Queue queue = inPlace ? null : createQueue();
        int size = size();
        for (int i = 0; i < size; i++) {
            Object element = dequeue();
            if ((i + 1) % n == 0) {
                if (!inPlace) {
                    queue.enqueue(element);
                }
                if (drop) {
                    continue;
                }
            }
            enqueue(element);
        }
        return queue;
    }

    @Override
    public Queue getNth(int n) {
        return nth(n, false, false);
    }

    @Override
    public void dropNth(int n) {
        nth(n, true, true);
    }

    @Override
    public Queue removeNth(int n) {
        return nth(n, true, false);
    }

    @Override
    public void clear() {
        size = 0;

        clearImpl();
    }
}
