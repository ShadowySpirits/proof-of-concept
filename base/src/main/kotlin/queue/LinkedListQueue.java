package queue;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A queue implement with Linked List
 *
 * @author ShadowySpirits
 */
public class LinkedListQueue<Item> implements Queue<Item> {

    private Node first;
    private Node last;
    private int n;

    @Override
    public boolean isEmpty() {
        return first == null;
    }

    @Override
    public int size() {
        return n;
    }

    @Override
    public void enqueue(Item item) {
        last = new Node(item);
        n++;
    }

    @Nullable
    @Override
    public Item dequeue() {
        if (isEmpty()) {
            return null;
        }
        Node old = first;
        first = first.next;
        if (--n == 0) {
            last = null;
        }
        return old.item;
    }

    @NotNull
    @Override
    public Iterator<Item> iterator() {
        return new ItemIterable();
    }

    private class Node {
        Item item;
        Node next;

        Node(Item item) {
            this.item = item;
            if (isEmpty()) {
                first = last = this;
                return;
            }
            last.next = this;
        }
    }

    private class ItemIterable implements Iterator<Item> {

        Node current = first;

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
