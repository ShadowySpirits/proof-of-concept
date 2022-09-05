package cdn

import org.junit.jupiter.api.Test

internal class ClientTest {

    @Test
    fun testRequest() {
        // 无缓存大小限制 0.80203 0.85267 0.8547
        // 缓存大小 20% 0.2405527 0.269918 0.330103
        val centerServer = CenterServer(TimeAndHeatBasedAlgo)
        val edgeServer = EdgeServer(centerServer)
        val client = Client(edgeServer)

        for (i in 0 until Constant.REQUEST_COUNT) {
            client.doRequest()
        }

        edgeServer.printCacheHitRate()
    }
}
