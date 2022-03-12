package queue

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ResizingArrayDequeTest {
    private lateinit var queue: ResizingArrayDeque<Int>

    @BeforeEach
    fun setUp() {
        queue = ResizingArrayDeque()
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
        queue.enqueue(1)
        assertEquals(queue.size(), 3)
        queue.enqueue(1)
        assertEquals(queue.size(), 4)
    }

    @Test
    fun testEnqueue() {
        queue.enqueue(1)
        assertEquals(queue.dequeue(), 1)
        queue.enqueue(1)
        assertEquals(queue.dequeue(), 1)
        queue.enqueue(1)
        assertEquals(queue.dequeue(), 1)
        queue.enqueue(1)
        queue.enqueue(2)
        queue.enqueue(3)
        queue.enqueue(4)
        assertEquals(queue.dequeue(), 1)
        assertEquals(queue.dequeue(), 2)
        assertEquals(queue.dequeue(), 3)
        assertEquals(queue.dequeue(), 4)
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
            ResizingArrayDeque<Int>().iterator().next()
        }
    }
}
