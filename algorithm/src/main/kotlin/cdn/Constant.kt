package cdn

object Constant {
    // 视频数量
    var VIDEO_COUNT = 1000
    // 用户请求视频片段的数量
    var REQUEST_COUNT = 100_00000

    // 最短视频长度，单位分钟
    var MIN_VIDEO_LENGTH = 10
    // 最长视频长度，单位分钟
    var MAX_VIDEO_LENGTH = 60

    // 没次请求预读缓存数量
    var CACHE_BATCH_SIZE = 2
    // 切换顺序预读和按热度预读的阈值
    var CACHE_HEAT_THRESHOLD = 0.05
    // 每个视频最大缓存比例
    var CACHE_MAX_SIZE_RATE = 0.20

    // 用户热度分布
    var USER_HEAT_THRESHOLD = 0.20
}
