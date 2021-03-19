package queue;

public class LinkedQueue extends AbstractQueue {
    private Node head = null, tail;

    @Override
    protected void enqueueImpl(final Object element) {
        if (tail == null) {
            tail = head = new Node(element);
        } else {
            tail = tail.next = new Node(element);
        }
    }

    @Override
    protected void dequeueImpl() {
        if (size == 0) {
            head = tail = null;
        } else {
            head = head.next;
        }
    }

    @Override
    public Object element() {
        return head.getElement();
    }

    @Override
    protected void clearImpl() {
        head = tail = null;
    }

    @Override
    protected Queue createQueue() {
        return new LinkedQueue();
    }

    private static class Node {
        private Node next;
        private final Object element;

        private Node(final Object element) {
            this.element = element;
        }

        private Object getElement() {
            return element;
        }
    }
}
