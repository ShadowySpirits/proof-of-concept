package queue

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LinkedListQueueTest {
    private lateinit var queue: LinkedListQueue<Int>

    @BeforeEach
    fun setUp() {
        queue = LinkedListQueue()
    }

    @Test
    fun testIsEmpty() {
        assertTrue(queue.isEmpty)
        queue.enqueue(1)
        assertFalse(queue.isEmpty)
    }

    @Test
    fun testSize() {
        assertEquals(queue.size(), 0)
        queue.enqueue(1)
        assertEquals(queue.size(), 1)
        queue.enqueue(1)
        assertEquals(queue.size(), 2)
    }

    @Test
    fun testEnqueue() {
        queue.enqueue(1)
        assertEquals(queue.dequeue(), 1)
        queue.enqueue(1)
        queue.enqueue(2)
        queue.enqueue(3)
        assertEquals(queue.dequeue(), 1)
        assertEquals(queue.dequeue(), 2)
        assertEquals(queue.dequeue(), 3)
    }

    @Test
    fun testDequeue() {
        assertNull(queue.dequeue())
    }

    @Test
    fun testIterator() {
        var i = 1
        queue.enqueue(1)
        queue.enqueue(2)
        queue.enqueue(3)
        for (item in queue) {
            assertEquals(item, i++)
        }
        assertThrows(NoSuchElementException::class.java) {
            LinkedListQueue<Int>().iterator().next()
        }
    }
}
