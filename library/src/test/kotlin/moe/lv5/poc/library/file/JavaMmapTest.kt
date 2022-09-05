package moe.lv5.poc.library.file

import org.junit.jupiter.api.Test
import java.io.File
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption

class JavaMmapTest {
    @Test
    fun test() {
        val fileChannel = FileChannel.open(
            Path.of("/tmp/test.txt"),
            StandardOpenOption.CREATE_NEW,
            StandardOpenOption.READ,
            StandardOpenOption.WRITE
        )
        val mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, (1024 * 1024 * 1024).toLong())
        val file = File("/tmp/test.txt")
        val rename = file.renameTo(File("/tmp/test2.txt"))
        println(rename)
        mappedByteBuffer.put(0, 'a'.code.toByte())
        println(mappedByteBuffer[0])
        println(File("/tmp/test1.txt").exists())
        println(File("/tmp/test2.txt").exists())
    }
}
