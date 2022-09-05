package moe.lv5.poc.library.cache.caffeine

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class CaffeineTest {

    @OptIn(ExperimentalTime::class)
    @Test
    fun asyncGetTest() {
        runBlocking {
            for (i in 1..3) {
                async {
                    val duration = measureTime {
                        assertNull(CaffeineCacheHolder.asyncGet(0))
                    }
                    println("async get 0: time $i cost ${duration}ms")
                }
            }
        }

        runBlocking {
            for (i in 1..3) {
                async {
                    val duration = measureTime {
                        assertEquals(1, CaffeineCacheHolder.asyncGet(1))
                    }
                    println("async get 1: time $i cost ${duration}ms")
                }
            }
        }
    }
}
