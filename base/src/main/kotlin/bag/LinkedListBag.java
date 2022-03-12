package bag;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.jetbrains.annotations.NotNull;

/**
 * A bag implement with Linked List
 *
 * @author ShadowySpirits
 */
public class LinkedListBag<Item> implements Bag<Item> {

    private Node top;
    private int n;

    @Override
    public boolean isEmpty() {
        return top == null;
    }

    @Override
    public int size() {
        return n;
    }

    @Override
    public void add(Item item) {
        top = new Node(item);
        n++;
    }

    @NotNull
    @Override
    public Iterator<Item> iterator() {
        return new ItemIterator();
    }

    private class Node {

        Item item;
        Node next;

        Node(Item item) {
            this.item = item;
            next = top;
        }
    }

    private class ItemIterator implements Iterator<Item> {

        private Node current = top;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @SuppressWarnings("Duplicates")
        @Override
        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Item item = current.item;
            current = current.next;
            return item;
        }
    }
}
