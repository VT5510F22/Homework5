package hw5.sets;

public class CoarseSet<T> implements Set<T>{
    private final Node<T> head;

    public CoarseSet() {
        head = new Node<>(Integer.MIN_VALUE);
        head.next = new Node<>(Integer.MAX_VALUE);
    }

    // The iteration code is repeated, but no simple way
    // to combine it all efficiently.
    @Override
    public boolean add(T item) {
        int key = item.hashCode();

        synchronized(this) {
            Node<T> pred = head;
            Node<T> curr = pred.next;

            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }

            if (key == curr.key) {
                return false;
            }

            Node<T> node = new Node<>(item, curr);
            pred.next = node;
            return true;
        }
    }

    @Override
    public boolean remove(T item) {
        int key = item.hashCode();

        synchronized(this) {
            Node<T> pred = head;
            Node<T> curr = pred.next;

            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }

            if (key == curr.key) {
                pred.next = curr.next;
                return true;
            }

            return false;
        }
    }

    @Override
    public boolean contains(T item) {
        int key = item.hashCode();

        synchronized(this) {
            Node<T> pred = head;
            Node<T> curr = pred.next;

            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }

            return key == curr.key;
        }
    };

    private static class Node<U> {
        int key;
        Node<U> next;

        public Node(U item, Node<U> next) {
            this.key = item.hashCode(); // don't actually need to hold on to the item!
            this.next = next;
        }

        public Node(int key) {
            this.key = key;
            next = null;
        }
    }
}
