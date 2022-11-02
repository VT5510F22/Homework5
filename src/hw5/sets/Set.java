package hw5.sets;

public interface Set<T> {
    /**
     * Adds item to set (no effect if already present)
     * @param item
     * @return {@code true} if added, {@code false} otherwise
     */
    boolean add(T item);

    /**
     * Removes item from set (if present)
     * @param item
     * @return {@code true} if item removed, {@code false} otherwise
     */
    boolean remove(T item);

    /**
     * Checks if item is in the set
     * @param item
     * @return {@code true} if item in set, {@code false} otherwise
     */
    boolean contains(T item);
}
