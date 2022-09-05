package moe.lv5.poc.library.mq.rocketmq

import com.aliyun.openservices.ons.api.Action
import com.aliyun.openservices.ons.api.ONSFactory
import com.aliyun.openservices.ons.api.PropertyKeyConst
import com.google.common.base.Stopwatch
import moe.lv5.poc.library.mq.rocketmq.auth.ClientRPCHook
import moe.lv5.poc.library.mq.rocketmq.auth.SessionCredentials
import moe.lv5.poc.library.mq.rocketmq.config.DailyConfig
import org.apache.rocketmq.client.consumer.*
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely
import org.apache.rocketmq.common.consumer.ConsumeFromWhere
import org.apache.rocketmq.common.message.MessageQueue
import org.apache.rocketmq.remoting.RPCHook
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
class ConsumerTest {

    private val config = DailyConfig

    private fun getAclRPCHook(): RPCHook {
        return ClientRPCHook(SessionCredentials(config.ACCESS_KEY, config.SECRET_KEY))
    }

    @Test
    fun consumeHangTest() {
        val consumer = DefaultMQPushConsumer(config.GROUP_ID, getAclRPCHook(), AllocateMessageQueueAveragely())
        consumer.namesrvAddr = config.NAMESRV_ADDR
        consumer.subscribe(config.TOPIC, "*")
        consumer.consumeFromWhere = ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET
        consumer.isVipChannelEnabled = false
        consumer.registerMessageListener(MessageListenerConcurrently { msgs, _ ->
//            println(msgs[0].storeSize)
//            println("${Thread.currentThread().name}  Receive New Messages: $msgs")
//            Thread.sleep(100000000)
            ConsumeConcurrentlyStatus.CONSUME_SUCCESS
        })
        consumer.start()
        println("Consumer Started.")
        while (true) {
        }
    }

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

//    @Ignore
//    @Test
//    fun popConsumeTest() {
//        val consumer = DefaultMQPullConsumer("", config.GROUP_ID, getAclRPCHook())
//        consumer.namesrvAddr = config.NAMESRV_ADDR
//        consumer.isVipChannelEnabled = false
//        consumer.isEnableStreamRequestType = false
//        consumer.start()
//
//        while (true) {
//            var popResult: PopResult?
//            val cost = measureTimeMillis {
//                popResult = consumer.pop(
//                    MessageQueue(config.TOPIC, "vip-cn-hangzhou-pre-lite-cluster-benchmark-0", 6),
//                    30000,
//                    5,
//                    config.GROUP_ID,
//                    9000000000000000,
//                    ConsumeInitMode.MAX
//                )
//            }
//            println("cost time: ${cost}s")
//            println("popStatus: ${popResult?.popStatus}")
//            println("restNum: ${popResult?.restNum}")
//            popResult?.msgFoundList?.forEach {
//                println(it)
//                if (it.queueId != 6) {
//                    consumer.ackMessage(config.TOPIC, config.GROUP_ID, it.getProperty(MessageConst.PROPERTY_POP_CK))
//                }
//                consumer.ackMessage(config.TOPIC, config.GROUP_ID, it.getProperty(MessageConst.PROPERTY_POP_CK))
//            }
//            Thread.sleep(10)
//        }
//    }

    @Test
    fun pullConsumerTest() {
        val consumer = DefaultMQPullConsumer("", config.GROUP_ID, getAclRPCHook())
        consumer.namesrvAddr = config.NAMESRV_ADDR
        consumer.isVipChannelEnabled = false
        consumer.isEnableStreamRequestType = false
        consumer.start()

        // 490720 492800
        val stopwatch = Stopwatch.createStarted()
        var offset = 799997L
        var pullResult = PullResult(PullStatus.NO_NEW_MSG, 0, 0, 0, emptyList())
        while (offset == 799997L) {
            try {
                pullResult = consumer.pull(MessageQueue(config.TOPIC, "broker-a", 0), "*", offset, 32)
            } catch (e: Exception) {
                Thread.sleep(1000)
                pullResult = consumer.pull(MessageQueue(config.TOPIC, "broker-a", 0), "*", offset, 32)
            }
            if (pullResult.nextBeginOffset == pullResult.maxOffset) {
                println("success")
                break
            }

            if (pullResult.nextBeginOffset != pullResult.maxOffset && pullResult.msgFoundList.size != 32) {
                println("msg count not equal to batch size, msg count: ${pullResult.msgFoundList.size}, batch size: 32")
                return
            }
            pullResult.msgFoundList.forEach { msg ->
                println(msg)
                if (msg.queueOffset != offset) {
                    println("msg queue offset not equal to request, msg queue offset: ${msg.queueOffset}, request: $offset")
                    return
                }
                offset++
            }

            if (pullResult.nextBeginOffset == pullResult.maxOffset) {
                println("success")
                return
            }
        }
        println("msg count: ${pullResult.nextBeginOffset}, cost: ${stopwatch.elapsed(TimeUnit.SECONDS)}")
    }

    @Test
    fun litePullConsumerTest() {
        val consumer = DefaultLitePullConsumer("", config.GROUP_ID, getAclRPCHook())
        consumer.namesrvAddr = config.NAMESRV_ADDR
        consumer.isVipChannelEnabled = false
        consumer.isEnableStreamRequestType = false
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
