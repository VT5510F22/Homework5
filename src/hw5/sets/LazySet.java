package hw5.sets;

public class LazySet<T> implements Set<T> {
    private final Node<T> head;

    public LazySet() {
        head = new Node<>(Integer.MIN_VALUE);
        head.next = new Node<>(Integer.MAX_VALUE);
    }

    private boolean validate(Node<T> pred, Node<T> curr) {
        return !pred.isMarked() && !curr.isMarked() && pred.next == curr;
    }

    @Override
    public boolean add(T item) {
        int key = item.hashCode();

        while (true) {
            Node<T> pred = head;
            Node<T> curr = head.next;

            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }

            synchronized(pred) {
                synchronized(curr) {
                    if (validate(pred, curr)) {
                        if (curr.key == key) {
                            return false;
                        }

                        Node<T> node = new Node<>(item, curr);
                        pred.next = node;
                        return true;
                    }
                }
            }
        }
    }

    @Override
    public boolean remove(T item) {
        int key = item.hashCode();

        while (true) {
            Node<T> pred = head;
            Node<T> curr = head.next;

            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }

            synchronized(pred) {
                synchronized(curr) {
                    if (validate(pred, curr)) {
                        if (curr.key != key) {
                            return false;
                        }

                        curr.mark();
                        pred.next = curr.next;
                        return true;
                    }
                }
            }
        }
    }

    @Override
    public boolean contains(T item) {
        int key = item.hashCode();
        Node<T> curr = head;

        while (curr.key < key) {
            curr = curr.next;
        }

        return curr.key == key && !curr.isMarked();
    }

    private static class Node<U> {
        int key; // volatile?
        Node<U> next; // volatile?
        private boolean marked; // volatile?

        public Node(U item, Node<U> next) {
            this.key = item.hashCode(); // don't actually need to hold on to the item!
            this.next = next;
            this.marked = false;
        }

        public Node(int key) {
            this.key = key;
            next = null;
            this.marked = false;
        }

        public void mark() {
            marked = true;
        }

        public boolean isMarked() {
            return marked;
        }
    }
}
