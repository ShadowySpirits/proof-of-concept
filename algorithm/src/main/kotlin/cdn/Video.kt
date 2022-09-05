package cdn

data class VideoSegment(
    val videoId: Int,
    val segmentId: Int,
    var viewCount: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (other is VideoSegment) {
            return videoId == other.videoId && segmentId == other.segmentId
        }
        return false
    }

    override fun hashCode(): Int {
        return "$videoId-$segmentId".hashCode()
    }
}

data class Video(val segments: List<VideoSegment>) {
    fun getViewCount(): Int = segments.sumOf { it.viewCount }
}
