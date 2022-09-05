package cdn

class LRUCache(private val capacity: Int): LinkedHashMap<Int, VideoSegment>(capacity, .75f, true) {

    fun put(segment: VideoSegment) {
        super.put(segment.segmentId, segment)
    }

    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Int, VideoSegment>?): Boolean {
        return size > capacity
    }
}
