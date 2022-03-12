package set

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class QuickUnionUFTest {
    private lateinit var uf: QuickUnionUF

    fun setUp() {
        val file = File("src/test/kotlin/set/mediumUF.txt")
        val reader = file.bufferedReader()
        uf = QuickUnionUF(reader.readLine().toInt())
        while (true) {
            reader.readLine()?.let {
                val p = it.substringBefore(' ').toInt()
                val q = it.substringAfter(' ').toInt()
                if (uf.connected(p, q)) return@let
                uf.union(p, q)
            } ?: break
        }
        reader.close()
    }

    @Test
    fun test() {
        setUp()
        assertEquals(3, uf.count())
    }
}
