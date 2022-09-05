package moe.lv5.poc.library.async.future

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

internal class JavaCompletableFutureTest {

    @Test
    fun baseTest() {
        try {
            val future = JavaCompletableFuture.base()
            assertFalse(future.isDone)
            println("non blocking")
            future.join()
        } catch (e: Exception) {
            println(e)
        }
    }
}
