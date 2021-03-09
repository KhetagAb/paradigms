package queue;

import java.util.Objects;

public class LinkedQueue extends AbstractQueue {
    private Node head = null, tail = null;

    @Override
    protected void enqueueImpl(final Object element) {
        if (Objects.isNull(tail)) {
            tail = head = new Node(element);
        } else {
            tail = tail.joinNext(new Node(element));
        }
    }

    @Override
    protected void dequeueImpl() {
        if (size == 0) {
            head = tail = null;
        } else {
            head = head.switchToNext();
        }
    }

    @Override
    protected void pushImpl(final Object element) {
        if (Objects.isNull(head)) {
            tail = head = new Node(element);
        } else {
            head = head.joinPrev(new Node(element));
        }
    }

    @Override
    protected void removeImpl() {
        if (size == 0) {
            head = tail = null;
        } else {
            tail = tail.switchToPrev();
        }
    }

    @Override
    public Object element() {
        assert !isEmpty();

        return head.getElement();
    }

    @Override
    public Object peek() {
        assert !isEmpty();

        return tail.getElement();
    }

    @Override
    protected void clearImpl() {
        head = tail = null;
    }

    @Override
    public Object[] toArray() {
        final Object[] array = new Object[size];

        int i = 0;
        for (Node node = head; Objects.nonNull(node.next); node = node.next) {
            array[i++] = node.element;
        }

        return array;
    }

    private static class Node {
        private Node next = this, prev = this;
        private final Object element;

        private Node(Object element) {
            this.element = element;
        }

        private Node joinNext(final Node next) {
            assert Objects.nonNull(next);

            this.next = next;
            next.prev = this;

            return next;
        }

        private Node joinPrev(final Node prev) {
            assert Objects.nonNull(prev);

            this.prev = prev;
            prev.next = this;

            return prev;
        }

        private Node switchToNext() {
            return next.prev = next;
        }

        private Node switchToPrev() {
            return prev.next = prev;
        }

        private Object getElement() {
            return element;
        }
    }
}
