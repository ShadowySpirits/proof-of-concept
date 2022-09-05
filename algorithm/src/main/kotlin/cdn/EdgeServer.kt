package cdn

import kotlin.math.roundToInt

class EdgeServer(private val centerServer: CenterServer) {
    private val cacheMap = HashMap<Int, LRUCache>()

    private var cacheHitCount = 0
    private var cacheMissCount = 0

    fun handleRequest(videoId: Int, videoSegmentId: Int): VideoSegment {
        if (cacheMap.containsKey(videoId) && cacheMap[videoId]!!.contains(videoSegmentId)) {
            cacheHitCount += 1
            return cacheMap[videoId]!![videoSegmentId]!!
        }

        cacheMissCount += 1

        val lruCache = cacheMap.computeIfAbsent(videoId) { LRUCache((getVideos()[videoId].segments.size * Constant.CACHE_MAX_SIZE_RATE).roundToInt()) }
        val videoSegments = centerServer.getVideoSegments(videoId, videoSegmentId, lruCache.values.toSet())
        videoSegments.forEach {
            lruCache.put(it)
        }

        centerServer.reportView(videoId, videoSegmentId)

        return videoSegments[0]
    }

    fun getVideos(): List<Video> {
        return centerServer.getVideos()
    }

    fun printCacheHitRate() {
        println(cacheHitCount * 1.0 / (cacheHitCount + cacheMissCount))
    }
}
