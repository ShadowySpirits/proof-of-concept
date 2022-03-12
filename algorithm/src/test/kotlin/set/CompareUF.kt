package set

import Watcher
import java.io.BufferedReader
import java.io.File
import kotlin.test.Test

class CompareUF {

    fun Double.format(digits: Int): String = java.lang.String.format("%.${digits}f", this)

    fun QF(reader: BufferedReader) {
        var totalTime = 0L
        var totalMEM = 0.0
        for (index in 1..1000) {
            val timer = Watcher()
            val uf = QuickFindUF(reader.readLine().toInt())
            while (true) {
                reader.readLine()?.let {
                    val p = it.substringBefore(' ').toInt()
                    val q = it.substringAfter(' ').toInt()
                    if (uf.connected(p, q)) return@let
                    uf.union(p, q)
                } ?: break
            }
            reader.reset()
            totalTime += timer.elapsedTime()
            totalMEM += timer.usedMemory()
        }
        totalMEM /= 1000
        println("QF:")
        println("time: $totalTime ms")
        println("MEM: ${totalMEM.format(4)} mb")
        println()
    }

    fun QU(reader: BufferedReader) {
        var totalTime = 0L
        var totalMEM = 0.0
        for (index in 1..1000) {
            val timer = Watcher()
            val uf = QuickUnionUF(reader.readLine().toInt())
            while (true) {
                reader.readLine()?.let {
                    val p = it.substringBefore(' ').toInt()
                    val q = it.substringAfter(' ').toInt()
                    if (uf.connected(p, q)) return@let
                    uf.union(p, q)
                } ?: break
            }
            reader.reset()
            totalTime += timer.elapsedTime()
            totalMEM += timer.usedMemory()
        }
        totalMEM /= 1000
        println("QU:")
        println("time: $totalTime ms")
        println("MEM: ${totalMEM.format(4)} mb")
        println()
    }

    fun WQU(reader: BufferedReader) {
        var totalTime = 0L
        var totalMEM = 0.0
        for (index in 1..1000) {
            val timer = Watcher()
            val uf = WeightedQuickUnionUF(reader.readLine().toInt())
            while (true) {
                reader.readLine()?.let {
                    val p = it.substringBefore(' ').toInt()
                    val q = it.substringAfter(' ').toInt()
                    if (uf.connected(p, q)) return@let
                    uf.union(p, q)
                } ?: break
            }
            reader.reset()
            totalTime += timer.elapsedTime()
            totalMEM += timer.usedMemory()
        }
        totalMEM /= 1000
        println("WQU:")
        println("time: $totalTime ms")
        println("MEM: ${totalMEM.format(4)} mb")
        println()
    }

    @Test
    fun compare() {
        val file = File("src/test/kotlin/set/mediumUF.txt")
        val reader = file.bufferedReader()
        reader.mark(file.length().toInt() + 1)
        QF(reader)
        QU(reader)
        WQU(reader)
        reader.close()
    }
}
