package string

import kotlin.test.Test
import kotlin.test.assertEquals

class KMPTest {

    @Test
    fun testGeneratePMT() {
        for ((i, s) in generatePMT("ababac").withIndex()) {
            when (i) {
                0 -> assertEquals(0, s)
                1 -> assertEquals(0, s)
                2 -> assertEquals(1, s)
                3 -> assertEquals(2, s)
                4 -> assertEquals(3, s)
                5 -> assertEquals(0, s)
            }
        }
        for ((i, s) in generatePMT("ABCDABD").withIndex()) {
            when (i) {
                0 -> assertEquals(0, s)
                1 -> assertEquals(0, s)
                2 -> assertEquals(0, s)
                3 -> assertEquals(0, s)
                4 -> assertEquals(1, s)
                5 -> assertEquals(2, s)
                6 -> assertEquals(0, s)
            }
        }
    }

    @Test
    fun testKMP() {
        assertEquals(0, kmp("cabcab", "cabcab"))
        assertEquals(7, kmp("ccccccccabcab", "cabcab"))
        assertEquals(-1, kmp("aaaaaaaaaaaaaaaa", "baaa"))
        assertEquals(9, kmp("abcacabcbcbacabc", "cbacabc"))
        assertEquals(17, kmp("HERE IS A SIMPLE EXAMPLE", "EXAMPLE"))
        assertEquals(15, kmp("BBC ABCDAB ABCDABCDABDE", "ABCDABD"))
    }
}
