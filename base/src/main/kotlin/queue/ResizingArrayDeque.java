package queue;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A circular queue implement with resizing array
 *
 * @author ShadowySpirits
 */

public class ResizingArrayDeque<Item> implements Queue<Item> {

    @SuppressWarnings("unchecked")
    private Item[] a = (Item[]) new Object[16];
    private int head;
    private int tail;

    @Override
    public boolean isEmpty() {
        return tail == head;
    }

    @Override
    public int size() {
        return (tail - head + a.length) % a.length;
    }

    private void resize(int len) {
        int n = a.length - head;
        @SuppressWarnings("unchecked")
        Item[] temp = (Item[]) new Object[len];
        //        for (int i = 0; i < a.length; i++) {
        //            temp[i] = a[i];
        //        }
        //         can use System.arraycopy() to instead manual array copy.
        System.arraycopy(a, head, temp, 0, n);
        System.arraycopy(a, 0, temp, n, tail);
        head = 0;
        tail = a.length - 1;
        a = temp;
    }

    @Override
    public void enqueue(Item item) {
        if ((tail + 1) % a.length == head) {
            resize(a.length * 2);
        }
        int temp = tail;
        tail = (tail + 1) % a.length;
        a[temp] = item;
    }

    @Nullable
    @Override
    public Item dequeue() {
        if (isEmpty()) {
            return null;
        }
        int temp = head;
        head = (head + 1) % a.length;
        return a[temp];
    }

    @NotNull
    @Override
    public Iterator<Item> iterator() {
        return new ItemIterable();
    }

    private class ItemIterable implements Iterator<Item> {

        int h = head;

        @Override
        public boolean hasNext() {
            return tail != h;
        }

        @SuppressWarnings("Duplicates")
        @Override
        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            int temp = h;
            h = (h + 1) % a.length;
            return a[temp];
        }
    }
}
