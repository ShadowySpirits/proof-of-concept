package cdn

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

object Utils {
    fun sleep(millis: Long) {
        runBlocking {
            delay(millis)
        }
    }
}
