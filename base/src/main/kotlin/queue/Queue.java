package queue;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * A FIFO(last-in, first-out) data  structure
 *
 * @author ShadowySpirits
 */
public interface Queue<Item> extends Iterable<Item> {

    /**
     * Check whether this queue is empty
     *
     * @return this queue is empty or not
     */
    @Contract(pure = true)
    boolean isEmpty();

    /**
     * Get the number of components in this queue
     *
     * @return the number of components in this queue
     */
    @Contract(pure = true)
    int size();

    /**
     * Enqueue an item to the end of this queue
     *
     * @param item the item expect to enqueue
     */
    void enqueue(Item item);

    /**
     * Dequeue the first item in this queue
     * or returns {@code null} if this queue is empty
     *
     * @return the first item in this queue or {@code null} if this queue is empty
     */
    @Nullable
    Item dequeue();
}
