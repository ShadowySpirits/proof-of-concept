package string

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ViolentMatchTest {

    @Test
    fun testviolentMatch() {
        assertEquals(0, violentMatch("cabcab", "cabcab"))
        assertEquals(7, violentMatch("ccccccccabcab", "cabcab"))
        assertEquals(-1, violentMatch("aaaaaaaaaaaaaaaa", "baaa"))
        assertEquals(9, violentMatch("abcacabcbcbacabc", "cbacabc"))
        assertEquals(17, violentMatch("HERE IS A SIMPLE EXAMPLE", "EXAMPLE"))
        assertEquals(15, violentMatch("BBC ABCDAB ABCDABCDABDE", "ABCDABD"))
    }
}
