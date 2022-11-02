package hw5.sets;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class LockFreeSet<T> implements Set<T> {
    private final Node<T> head;

    public LockFreeSet() {
        Node<T> tail = new Node<>(Integer.MAX_VALUE);
        head = new Node<>(Integer.MIN_VALUE, tail);
    }

    @Override
    public boolean add(T item) {
        int key = item.hashCode();

        while (true) {
            Window<T> window = find(head, key);
            Node<T> pred = window.pred;
            Node<T> curr = window.curr;

            if (curr.key == key) {
                return false;
            }

            Node<T> node = new Node<>(item, curr);
            if (pred.next.compareAndSet(curr, node, false, false)) {
                return true;
            }
        }
    }

    @Override
    public boolean remove(T item) {
        int key = item.hashCode();
        boolean snip;

        while (true) {
            Window<T> window = find(head, key);
            Node<T> pred = window.pred;
            Node<T> curr = window.curr;

            if (curr.key != key) {
                return false;
            }

            Node<T> succ = curr.next.getReference();
            snip = curr.next.compareAndSet(succ, succ, false, true);

            if (!snip) {
                continue;
            }

            pred.next.compareAndSet(curr, succ, false, false);
            return true;
        }
    }

    @Override
    public boolean contains(T item) {
        boolean[] marked = {false};
        int key = item.hashCode();
        Node<T> curr = head;

        while (curr.key < key) {
            curr = curr.next.getReference();
            curr.next.get(marked);
        }

        return curr.key == key && !marked[0];
    }

    private Window<T> find(Node<T> head, int key) {
        Node<T> pred, curr, succ;
        boolean[] marked = {false};
        boolean snip;

        retry: while (true) {
            pred = head;
            curr = pred.next.getReference();

            while (true) {
                succ = curr.next.get(marked);

                while (marked[0]) {
                    snip = pred.next.compareAndSet(curr, succ, false, false);

                    if (!snip) {
                        continue retry;
                    }

                    curr = succ;
                    succ = curr.next.get(marked);
                }
                if (curr.key >= key) {
                    return new Window<>(pred, curr);
                }

                pred = curr;
                curr = succ;
            }
        }
    }

    private static class Node<U> {
        int key;
        AtomicMarkableReference<Node<U>> next;

        public Node(U item, Node<U> next) {
            this.key = item.hashCode(); // don't actually need to hold on to the item!
            this.next = new AtomicMarkableReference<>(next, false);
        }

        public Node(int key, Node<U> next) {
            this.key = key;
            this.next = new AtomicMarkableReference<>(next, false);
        }

        public Node(int key) {
            this(key, null);
        }
    }

    private static class Window<U> {
        public Node<U> pred;
        public Node<U> curr;

        public Window(Node<U> pred, Node<U> curr) {
            this.pred = pred;
            this.curr = curr;
        }
    }
}