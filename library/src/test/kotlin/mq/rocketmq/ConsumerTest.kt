package mq.rocketmq

import com.aliyun.openservices.ons.api.Action
import com.aliyun.openservices.ons.api.ONSFactory
import com.aliyun.openservices.ons.api.PropertyKeyConst
import mq.rocketmq.auth.ClientRPCHook
import mq.rocketmq.auth.SessionCredentials
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely
import org.apache.rocketmq.common.consumer.ConsumeFromWhere
import org.apache.rocketmq.common.message.MessageQueue
import org.apache.rocketmq.remoting.RPCHook
import java.util.*
import kotlin.test.Ignore
import kotlin.test.Test


class ConsumerTest {

    private val config = QDInternetConfig

    private fun getAclRPCHook(): RPCHook {
        return ClientRPCHook(SessionCredentials(config.ACCESS_KEY, config.SECRET_KEY))
    }

    @Ignore
    @Test
    fun consumeHangTest() {
        val consumer = DefaultMQPushConsumer(config.GROUP_ID, getAclRPCHook(), AllocateMessageQueueAveragely())
        consumer.namesrvAddr = config.NAMESRV_ADDR
        consumer.subscribe(config.TOPIC, "*")
        consumer.consumeFromWhere = ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET
        consumer.isVipChannelEnabled = false
        consumer.registerMessageListener(MessageListenerConcurrently { msgs, _ ->
            println("${Thread.currentThread().name}  Receive New Messages: $msgs")
            try {
                Thread.sleep(100000000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            ConsumeConcurrentlyStatus.RECONSUME_LATER
        })
        consumer.start()
        println("Consumer Started.")
        while (true) {
        }
    }

    @Ignore
    @Test
    fun consumeTest() {
        val consumer = DefaultMQPushConsumer(config.GROUP_ID, getAclRPCHook(), AllocateMessageQueueAveragely())
        consumer.namesrvAddr = config.NAMESRV_ADDR
        consumer.subscribe(config.TOPIC, "*")
        consumer.consumeFromWhere = ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET
        consumer.isVipChannelEnabled = false
        consumer.registerMessageListener(MessageListenerConcurrently { msgs, _ ->
            println("${Thread.currentThread().name}  Receive New Messages: $msgs")
            ConsumeConcurrentlyStatus.CONSUME_SUCCESS
        })
        consumer.start()
        println("Consumer Started.")
        while (true) {
        }
    }

    @Ignore
    @Test
    fun pullConsumerTest() {
        val consumer = DefaultMQPullConsumer("", config.GROUP_ID, getAclRPCHook())
        consumer.namesrvAddr = config.NAMESRV_ADDR
        consumer.isVipChannelEnabled = false
//        consumer.isEnableStreamRequestType = false
        consumer.start()

        val pullResult =
            consumer.pullBlockIfNotFound(MessageQueue(config.TOPIC, "ap-southeast-3-share-01-0", 0), "*", 23111, 100)
        println(pullResult)
    }

    @Ignore
    @Test
    fun litePullConsumerTest() {
        val consumer = DefaultLitePullConsumer("", config.GROUP_ID, getAclRPCHook())
        consumer.namesrvAddr = config.NAMESRV_ADDR
        consumer.isVipChannelEnabled = false
//        consumer.isEnableStreamRequestType = false
        consumer.subscribe(config.TOPIC, "*")
        consumer.start()
        println("Consumer Started.")
        while (true) {
            val messageList = consumer.poll()
            messageList.forEach {
                println("Receive New Messages: $it")
            }
        }
    }

    @Ignore
    @Test
    fun aliyunConsumerTest() {
        val properties = Properties()
        properties[PropertyKeyConst.GROUP_ID] = config.GROUP_ID
        properties[PropertyKeyConst.AccessKey] = config.ACCESS_KEY
        properties[PropertyKeyConst.SecretKey] = config.SECRET_KEY
        properties[PropertyKeyConst.NAMESRV_ADDR] = config.NAMESRV_ADDR
        val consumer = ONSFactory.createConsumer(properties)

        consumer.subscribe(config.TOPIC.substringAfter('%'), "*") { message, context ->
            println("Receive: $message")
            return@subscribe Action.CommitMessage
        }

        consumer.start()
        println("Consumer Started")
        while (true) {
        }
    }

    @Ignore
    @Test
    fun aliyunRAMConsumerTest() {
        val properties = Properties()
        properties[PropertyKeyConst.GROUP_ID] = config.GROUP_ID
        properties[PropertyKeyConst.RAM_ROLE_NAME] = config.RAM_ROLE_NAME
        properties[PropertyKeyConst.NAMESRV_ADDR] = config.NAMESRV_ADDR
        val consumer = ONSFactory.createConsumer(properties)

        consumer.subscribe(config.TOPIC.substringAfter('%'), "*") { message, context ->
            println("Receive: $message")
            return@subscribe Action.CommitMessage
        }

        consumer.start()
        println("Consumer Started")
        while (true) {
        }
    }
}
