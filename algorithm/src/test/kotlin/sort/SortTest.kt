package sort

import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class SortTest {
    private val init = getRandomArray()
    private lateinit var a: Array<Int>
    private lateinit var b: Array<Int>

    companion object {
        private const val ARRAY_COUNT = 10_000
    }

    private fun getRandomArray(): Array<Int> {
        val ra = Random()
        return Array(ARRAY_COUNT) {
            return@Array ra.nextInt(ARRAY_COUNT * 10)
        }
    }

    private fun getOrderedArray(): Array<Int> {
        var i = 0
        return Array(ARRAY_COUNT) {
            return@Array i++
        }
    }

    @BeforeTest
    fun setUp() {
        a = init.copyOf()
        b = a.copyOf()
        b.sort()
    }

    @Test
    fun testBubbleSort() {
        bubbleSort(a)
        assertTrue(a.contentEquals(b))
    }

    @Test
    fun testInsertSort() {
        insertionSort(a)
        assertTrue(a.contentEquals(b))
    }

    @Test
    fun testShellSort() {
        shellSort(a)
        assertTrue(a.contentEquals(b))
    }

    @Test
    fun testSelectionSort() {
        selectionSort(a)
        assertTrue(a.contentEquals(b))
    }


    @Test
    fun testMergeSort() {
        mergeSort(a)
        assertTrue(a.contentEquals(b))
    }

    @Test
    fun testQuickSort() {
        quickSort(a)
        assertTrue(a.contentEquals(b))
    }
}
