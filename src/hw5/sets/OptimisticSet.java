package hw5.sets;

public class OptimisticSet<T> implements Set<T> {
    private final Node<T> head;

    public OptimisticSet() {
        head = new Node<>(Integer.MIN_VALUE);
        head.next = new Node<>(Integer.MAX_VALUE);
    }

    private boolean validate(Node<T> pred, Node<T> curr) {
        Node<T> node = head;

        while (node.key <= pred.key) {
            if (node == pred) {
                return pred.next == curr;
            }

            node = node.next;
        }

        return false;
    }

    @Override
    public boolean add(T item) {
        int key = item.hashCode();

        while (true) {
            Node<T> pred = head;
            Node<T> curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }

            synchronized(pred) {
                synchronized(curr) {
                    if (validate(pred, curr)) {
                        if (curr.key == key) {
                            return false;
                        } else {
                            Node<T> node = new Node<>(item, curr);
                            pred.next = node;
                            return true;
                        }
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
            Node<T> curr = pred.next;

            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }

            synchronized(pred) {
                synchronized(curr) {
                    if (validate(pred, curr)) {
                        if (curr.key == key) {
                            pred.next = curr.next;
                            return true;
                        }

                        return false;
                    }
                }
            }
        }
    }

    @Override
    public boolean contains(T item) {
        int key = item.hashCode();
        while (true) {
            Node<T> pred = head;
            Node<T> curr = pred.next;

            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }

            synchronized(pred) {
                synchronized(curr) {
                    if (validate(pred, curr)) {
                        return curr.key == key;
                    }
                }
            }
        }
    }

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
