package moe.lv5.poc.library.cache.caffeine

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CaffeineCacheHolder {
    val syncCache: LoadingCache<Int, Int?> = Caffeine.newBuilder()
        .build {
            Thread.sleep(3 * 1000)
            return@build if (it == 0) null else 1
        }

    suspend fun asyncGet(i: Int): Int? = withContext(Dispatchers.IO) {
        syncCache.get(i)
    }
}
