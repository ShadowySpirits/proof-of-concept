package stack

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LinkedListStackTest {
    private lateinit var stack: LinkedListStack<Int>

    @BeforeEach
    fun setUp() {
        stack = LinkedListStack()
    }

    @Test
    fun testIsEmpty() {
        assertTrue(stack.isEmpty)
        stack.push(1)
        assertFalse(stack.isEmpty)
    }

    @Test
    fun testSize() {
        assertEquals(stack.size(), 0)
        stack.push(1)
        assertEquals(stack.size(), 1)
        stack.push(1)
        assertEquals(stack.size(), 2)
    }

    @Test
    fun testPush() {
        stack.push(1)
        assertEquals(stack.pop(), 1)
        stack.push(1)
        stack.push(2)
        stack.push(3)
        assertEquals(stack.peek(), 3)
        assertEquals(stack.pop(), 3)
        assertEquals(stack.peek(), 2)
        assertEquals(stack.pop(), 2)
        assertEquals(stack.peek(), 1)
        assertEquals(stack.pop(), 1)
    }

    @Test
    fun testPop() {
        assertNull(stack.pop())
    }

    @Test
    fun testPeek() {
        assertNull(stack.peek())
    }

    @Test
    fun testIterator() {
        var i = 3
        stack.push(1)
        stack.push(2)
        stack.push(3)
        for (item in stack) {
            assertEquals(item, i--)
        }
        assertThrows(NoSuchElementException::class.java) {
            LinkedListStack<Int>().iterator().next()
        }
    }
}
