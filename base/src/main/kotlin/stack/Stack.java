package stack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * A LIFO(last-in, first-out) data  structure
 *
 * @author ShadowySpirits
 */
public interface Stack<Item> extends Iterable<Item> {

    /**
     * Check whether this stack is empty
     *
     * @return this stack is empty or not
     */
    @Contract(pure = true)
    boolean isEmpty();

    /**
     * Get the number of components in this stack
     *
     * @return he number of components in this stack
     */
    @Contract(pure = true)
    int size();

    /**
     * Push an item onto the top of the stack
     *
     * @param item the item expect to push onto this stack
     */
    void push(Item item);

    /**
     * Remove the first item in the stack
     * or return {@code null} if the stack is empty
     *
     * @return the top item in this stack or {@code null} if this queue is empty
     */
    @Nullable
    Item pop();

    /**
     * Retrieve, but do not remove, the top of this stack,
     * or return {@code null} if this stack is empty.
     *
     * @return the top item in this stack, or {@code null} if this stack is empty
     */
    @Nullable
    Item peek();
}
