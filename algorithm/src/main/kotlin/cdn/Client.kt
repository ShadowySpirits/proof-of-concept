package cdn

import kotlin.math.roundToInt
import kotlin.random.Random

class Client(val edgeServer: EdgeServer) {
    val videoMap = mutableListOf<Pair<List<Int>, List<Int>>>()

    init {
        edgeServer.getVideos().forEach {
            val pair = Pair<MutableList<Int>, MutableList<Int>>(mutableListOf(), mutableListOf())
            for (i in 0 until (it.segments.size * (1 - Constant.USER_HEAT_THRESHOLD)).roundToInt()) {
                pair.first.add(Random.nextInt(0, it.segments.size))
            }

            for (i in 0 until (it.segments.size * Constant.CACHE_HEAT_THRESHOLD).roundToInt()) {
                pair.first.add(i)
            }

            for (i in 0 until it.segments.size) {
                if (!pair.first.contains(i)) {
                    pair.second.add(i)
                }
            }
            videoMap.add(pair)
        }
    }

    fun doRequest() {
        val videoId = if (Random.nextInt(0, 100) > 100 * Constant.USER_HEAT_THRESHOLD) {
            Random.nextInt(0, (videoMap.size * Constant.USER_HEAT_THRESHOLD).toInt())
        } else Random.nextInt((videoMap.size * Constant.USER_HEAT_THRESHOLD).toInt(), videoMap.size)

        val segmentId = if (Random.nextInt(0, 100) > 100 * Constant.USER_HEAT_THRESHOLD) {
            videoMap[videoId].first[Random.nextInt(0, videoMap[videoId].first.size)]
        } else videoMap[videoId].second[Random.nextInt(0, videoMap[videoId].second.size)]

        edgeServer.handleRequest(videoId, segmentId)
    }
}
