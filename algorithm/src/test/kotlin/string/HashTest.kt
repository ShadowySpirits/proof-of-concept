package string

import kotlin.test.Test

class HashTest {

    @Test
    fun testNumericStringHash() {
        val hashCodeSet = mutableSetOf<Int>()

        for (i in 100000..999999) {
            hashCodeSet.add(i.toString().hashCode())
        }

        println("hashCode duplicate nums: ${900000 - hashCodeSet.size}")
    }
}
