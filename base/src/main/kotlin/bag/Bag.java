package bag;

import org.jetbrains.annotations.Contract;

/**
 * A collection where removing items is not supported
 *
 * @author ShadowySpirits
 */
public interface Bag<Item> extends Iterable<Item> {

    /**
     * Check whether this bag is empty
     *
     * @return this bag is empty or not
     */
    @Contract(pure = true)
    boolean isEmpty();

    /**
     * Get the number of components in this bag
     *
     * @return he number of components in this bag
     */
    @Contract(pure = true)
    int size();

    /**
     * Insert an item in this bag
     *
     * @param item the item expect to add in this bag
     */
    void add(Item item);
}
