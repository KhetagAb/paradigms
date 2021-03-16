package queue;

import java.util.Objects;

public class LinkedQueue extends AbstractQueue {
    private Node head = null, tail = null;

    @Override
    protected void enqueueImpl(final Object element) {
        if (Objects.isNull(tail)) {
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
        private Node next = this;
        private final Object element;

        private Node(Object element) {
            this.element = element;
        }

        private Object getElement() {
            return element;
        }
    }
}
