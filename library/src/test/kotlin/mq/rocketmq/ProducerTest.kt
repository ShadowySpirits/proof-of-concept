package mq.rocketmq

import mq.rocketmq.auth.ClientRPCHook
import mq.rocketmq.auth.SessionCredentials
import org.apache.rocketmq.client.producer.DefaultMQProducer
import org.apache.rocketmq.common.message.Message
import org.apache.rocketmq.remoting.RPCHook
import org.apache.rocketmq.remoting.common.RemotingHelper
import kotlin.test.Ignore
import kotlin.test.Test

class ProducerTest {

    private val config = SE3Config

    private fun getAclRPCHook(): RPCHook {
        return ClientRPCHook(SessionCredentials(config.ACCESS_KEY, config.SECRET_KEY))
    }


    @Ignore
    @Test
    fun test() {
        val producer = DefaultMQProducer(config.GROUP_ID, getAclRPCHook())
        producer.namesrvAddr = config.NAMESRV_ADDR
        producer.isVipChannelEnabled = false
        producer.start()
        while (true) {
            try {
                val msg = Message(config.TOPIC,
                    config.TAG,
                    "Hello world".toByteArray(charset(RemotingHelper.DEFAULT_CHARSET)))
                val sendResult = producer.sendOneway(msg)
                println(sendResult)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        producer.shutdown()
    }
}
