package mq.rocketmq

import com.aliyun.openservices.ons.api.ONSFactory
import com.aliyun.openservices.ons.api.PropertyKeyConst
import mq.rocketmq.auth.ClientRPCHook
import mq.rocketmq.auth.SessionCredentials
import org.apache.rocketmq.client.producer.DefaultMQProducer
import org.apache.rocketmq.common.message.Message
import org.apache.rocketmq.remoting.RPCHook
import org.apache.rocketmq.remoting.common.RemotingHelper
import java.util.*
import kotlin.test.Ignore
import kotlin.test.Test


class ProducerTest {

    private val config = QDInternetConfig

    private fun getAclRPCHook(): RPCHook {
        return ClientRPCHook(SessionCredentials(config.ACCESS_KEY, config.SECRET_KEY))
    }


    @Ignore
    @Test
    fun ossProducerTest() {
        val producer = DefaultMQProducer(config.GROUP_ID, getAclRPCHook())
        producer.namesrvAddr = config.NAMESRV_ADDR
        producer.isVipChannelEnabled = false
        producer.start()
        try {
            val msg = Message(config.TOPIC,
                config.TAG,
                "Hello world".toByteArray(charset(RemotingHelper.DEFAULT_CHARSET)))
            val sendResult = producer.sendOneway(msg)
            println(sendResult)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        producer.shutdown()
    }

    @Ignore
    @Test
    fun aliyunProducerTest() {
        val properties = Properties()
        properties[PropertyKeyConst.AccessKey] = config.ACCESS_KEY
        properties[PropertyKeyConst.SecretKey] = config.SECRET_KEY
        properties[PropertyKeyConst.NAMESRV_ADDR] = config.NAMESRV_ADDR
        val producer = ONSFactory.createProducer(properties)
        producer.start()

        try {
            val msg = com.aliyun.openservices.ons.api.Message(config.TOPIC.substringAfter('%'),
                config.TAG,
                "Hello MQ".toByteArray())
            val sendResult = producer.send(msg)
            if (sendResult != null) {
                println(Date().toString() + " Send mq message success. Topic is: " + config.TOPIC + " msgId is: " + sendResult.messageId)
            }
        } catch (e: java.lang.Exception) {
            println(Date().toString() + " Send mq message failed. Topic is:" + config.TOPIC)
            e.printStackTrace()
        }
        producer.shutdown()
    }

    @Ignore
    @Test
    fun aliyunRAMProducerTest() {
        val properties = Properties()
        properties[PropertyKeyConst.RAM_ROLE_NAME] = config.RAM_ROLE_NAME
        properties[PropertyKeyConst.NAMESRV_ADDR] = config.NAMESRV_ADDR
        val producer = ONSFactory.createProducer(properties)
        producer.start()

        try {
            val msg = com.aliyun.openservices.ons.api.Message(config.TOPIC.substringAfter('%'),
                config.TAG,
                "Hello MQ".toByteArray())
            val sendResult = producer.send(msg)
            if (sendResult != null) {
                println(Date().toString() + " Send mq message success. Topic is: " + config.TOPIC + " msgId is: " + sendResult.messageId)
            }
        } catch (e: java.lang.Exception) {
            println(Date().toString() + " Send mq message failed. Topic is:" + config.TOPIC)
            e.printStackTrace()
        }
        producer.shutdown()
    }
}
