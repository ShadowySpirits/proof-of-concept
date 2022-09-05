package moe.lv5.poc.library.mq.rocketmq

import moe.lv5.poc.library.mq.rocketmq.auth.ClientRPCHook
import moe.lv5.poc.library.mq.rocketmq.auth.SessionCredentials
import moe.lv5.poc.library.mq.rocketmq.config.DailyConfig
import org.apache.rocketmq.common.message.MessageQueue
import org.apache.rocketmq.remoting.RPCHook
import org.apache.rocketmq.tools.admin.DefaultMQAdminExt
import java.text.SimpleDateFormat
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
class MQAdminTest {

    private val config = DailyConfig

    private fun getAclRPCHook(): RPCHook {
        return ClientRPCHook(SessionCredentials(config.ACCESS_KEY, config.SECRET_KEY))
    }

    @Test
    fun queryMsgTest() {
        val mqAdmin = DefaultMQAdminExt(getAclRPCHook())
        mqAdmin.namesrvAddr = config.NAMESRV_ADDR
        mqAdmin.isVipChannelEnabled = false
        mqAdmin.start()
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val begin = df.parse("2022-06-01 00:00:00").time
        val end = df.parse("2022-06-13 00:00:00").time
        val queryResult = mqAdmin.queryMessage(config.TOPIC, "AC16F19260684E25154F31D89F880246", 1, begin, end)
        println(queryResult)
    }

    @Test
    fun resetOffsetByQueueIdTest() {
        val mqAdmin = DefaultMQAdminExt(getAclRPCHook())
        mqAdmin.namesrvAddr = config.NAMESRV_ADDR
        mqAdmin.isVipChannelEnabled = false
        mqAdmin.start()

//        var queryResult = mqAdmin.resetOffsetByQueueId("100.103.40.183:8080", config.GROUP_ID, config.TOPIC, 0, 33630000)
        // 36713108
//        mqAdmin.resetOffsetByQueueId("100.103.40.183:8080", config.GROUP_ID, config.TOPIC, 6, 36713108)
//        println(queryResult)
    }

    @Test
    fun resetOffsetByTimeStampTest() {
        val mqAdmin = DefaultMQAdminExt(getAclRPCHook())
        mqAdmin.namesrvAddr = config.NAMESRV_ADDR
        mqAdmin.isVipChannelEnabled = false
        mqAdmin.start()


        // 0 0 0
        // 1661848945100 249148 249150
        // 1661861913885 799997 800000
        // now 2175710 2175709
        val searchOffset =
            mqAdmin.searchOffset(MessageQueue(config.TOPIC, "broker-a", 0), 1661861913885)
        println(searchOffset)

//        var result =
//            mqAdmin.queryMessage(config.TOPIC, "AC135D5800002A9F0000000069A67936", 32, 0, System.currentTimeMillis())
//        println(result)

//        var result1 =
//            mqAdmin.queryAllMessage("DefaultCluster", config.TOPIC, "AC135D30DFC05FFD2B2799B40D1D47E9")
//        println(result1.messageList.size)

//        val queryResult = mqAdmin.resetOffsetByTimestamp(config.TOPIC, config.GROUP_ID, 1655275002131L, true)
//        println(queryResult)
    }
}
