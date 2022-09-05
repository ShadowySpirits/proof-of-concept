package cdn

interface CacheAlgo {
    fun getCacheSegments(video: Video, requestSegmentId: Int, filterSet: Set<VideoSegment>): List<VideoSegment>
}

object RequestBasedCacheAlgo : CacheAlgo {
    override fun getCacheSegments(video: Video, requestSegmentId: Int, filterSet: Set<VideoSegment>): List<VideoSegment> {
        return emptyList()
    }
}

object TimeBasedCacheAlgo : CacheAlgo {
    override fun getCacheSegments(video: Video, requestSegmentId: Int, filterSet: Set<VideoSegment>): List<VideoSegment> {
        val res = mutableListOf<VideoSegment>()
        val segmentList = (video.segments.subList(requestSegmentId, video.segments.size - 1).toSet() subtract filterSet).sortedBy { it.segmentId }
        for (i in 0 until Constant.CACHE_BATCH_SIZE) {
            if (requestSegmentId + i < segmentList.size) {
                res.add(segmentList[requestSegmentId + i])
            }
        }
        return res
    }
}

object TimeAndHeatBasedAlgo: CacheAlgo {
    override fun getCacheSegments(video: Video, requestSegmentId: Int, filterSet: Set<VideoSegment>): List<VideoSegment> {
        val res = mutableListOf<VideoSegment>()

        if (video.segments.size * Constant.CACHE_HEAT_THRESHOLD > requestSegmentId) {
            return TimeBasedCacheAlgo.getCacheSegments(video, requestSegmentId, filterSet)
        }

        val segmentList = (video.segments.toSet() subtract filterSet).sortedBy { -it.viewCount }
        for (i in 0 until Constant.CACHE_BATCH_SIZE) {
            if (i < segmentList.size) {
                res.add(segmentList[i])
            }
        }
        return res
    }
}
