package stack

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ResizingArrayStackTest {

    private lateinit var stack: ResizingArrayStack<Int>

    @BeforeEach
    fun setUp() {
        stack = ResizingArrayStack()
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

    @Suppress("UNCHECKED_CAST")
    @Test
    fun testPush() {
        stack.push(1)
        assertEquals(stack.pop(), Integer.valueOf(1))
        stack.push(1)
        stack.push(2)
        stack.push(3)
        stack.push(1)
        stack.push(2)
        stack.push(3)
        stack.push(1)
        stack.push(2)
        stack.push(3)
        assertEquals(stack.peek(), 3)
        assertEquals(stack.pop(), 3)
        assertEquals(stack.peek(), 2)
        assertEquals(stack.pop(), 2)
        assertEquals(stack.peek(), 1)
        assertEquals(stack.pop(), 1)

        stack.pop()
        stack.pop()
        stack.pop()
//        val field = stack::class.memberProperties.findLast { it.name.equals("a") }
//        field!!.isAccessible = true
//        val a = (field as KProperty1<stack.ResizingArrayStack<Int>, Array<Object>>).get(stack)
        val field = ResizingArrayStack::class.java.getDeclaredField("a")
        field.isAccessible = true
        val a = field.get(stack) as Array<Any>
        assertEquals(a.size, 8)
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
            ResizingArrayStack<Int>().iterator().next()
        }
    }
}
