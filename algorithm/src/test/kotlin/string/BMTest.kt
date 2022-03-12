package string

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BMTest {

    @Test
    fun testGenerateBC() {
        for ((i, s) in generateBC("cabcab").withIndex()) {
            when (i) {
                97 -> assertEquals(4, s)
                98 -> assertEquals(5, s)
                99 -> assertEquals(3, s)
            }
        }
    }

    @Test
    fun testGenerateGS() {
        val (suffix, prefix) = generateGS("cabcab")
        for ((i, s) in suffix.withIndex()) {
            when (i) {
                1 -> assertEquals(2, s)
                2 -> assertEquals(1, s)
                3 -> assertEquals(0, s)
                4 -> assertEquals(-1, s)
                5 -> assertEquals(-1, s)
            }
        }
        for ((i, s) in prefix.withIndex()) {
            when (i) {
                1 -> assertFalse(s)
                2 -> assertFalse(s)
                3 -> assertTrue(s)
                4 -> assertFalse(s)
                5 -> assertFalse(s)
            }
        }
    }

    @Test
    fun testBM() {
        assertEquals(0, bm("cabcab", "cabcab"))
        assertEquals(7, bm("ccccccccabcab", "cabcab"))
        assertEquals(-1, bm("aaaaaaaaaaaaaaaa", "baaa"))
        assertEquals(9, bm("abcacabcbcbacabc", "cbacabc"))
        assertEquals(17, bm("HERE IS A SIMPLE EXAMPLE", "EXAMPLE"))
        assertEquals(15, bm("BBC ABCDAB ABCDABCDABDE", "ABCDABD"))
    }
}
