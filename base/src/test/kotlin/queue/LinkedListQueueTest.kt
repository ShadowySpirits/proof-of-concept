package queue

import kotlin.test.*

class LinkedListQueueTest {
    private lateinit var queue: LinkedListQueue<Int>

    @BeforeTest
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
        assertFailsWith<NoSuchElementException> {
            LinkedListQueue<Int>().iterator().next()
        }
    }
}
