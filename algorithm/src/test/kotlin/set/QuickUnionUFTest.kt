package set

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

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
