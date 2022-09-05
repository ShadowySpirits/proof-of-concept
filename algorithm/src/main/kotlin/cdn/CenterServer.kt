package cdn

import kotlin.random.Random

class CenterServer(private val cacheAlgo: CacheAlgo) {

    private val videos: List<Video>

    init {
        videos = mutableListOf()
        for (i in 0 until Constant.VIDEO_COUNT) {
            val video = Video(mutableListOf())
            for (j in 0 until Random.nextInt(Constant.MIN_VIDEO_LENGTH, Constant.MAX_VIDEO_LENGTH)) {
                (video.segments as MutableList).add(VideoSegment(i, j))
            }
            videos.add(video)
        }
    }

    fun getVideoSegments(videoId: Int, segmentId: Int, filterSet: Set<VideoSegment>): List<VideoSegment> {
        if (videoId >= videos.size) {
            throw IllegalArgumentException()
        }

        val video = videos[videoId]
        val res = mutableListOf(video.segments[segmentId])
        res.addAll(cacheAlgo.getCacheSegments(video, segmentId, filterSet))

        return res
    }

    fun reportView(videoId: Int, segmentId: Int) {
        videos[videoId].segments[segmentId].viewCount += 1
    }

    fun getVideos(): List<Video> {
        return videos
    }
}
