package bag

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LinkedListBagTest {
    private lateinit var stack: LinkedListBag<Int>

    @BeforeEach
    fun setUp() {
        stack = LinkedListBag()
    }

    @Test
    fun testIsEmpty() {
        assertTrue(stack.isEmpty)
        stack.add(1)
        assertFalse(stack.isEmpty)
    }

    @Test
    fun testSize() {
        assertEquals(stack.size(), 0)
        stack.add(1)
        assertEquals(stack.size(), 1)
        stack.add(1)
        assertEquals(stack.size(), 2)
    }

    @Test
    fun testAdd() {
        stack.add(1)
        stack.add(2)
        stack.add(3)
    }

    @Test
    fun testIterator() {
        var i = 3
        testAdd()
        for (item in stack) {
            assertEquals(item, i--)
        }
        assertThrows(NoSuchElementException::class.java) {
            LinkedListBag<Int>().iterator().next()
        }
    }
}
