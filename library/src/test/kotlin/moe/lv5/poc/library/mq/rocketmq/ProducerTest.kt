package moe.lv5.poc.library.mq.rocketmq

import com.aliyun.openservices.ons.api.ONSFactory
import com.aliyun.openservices.ons.api.PropertyKeyConst
import com.google.common.util.concurrent.RateLimiter
import moe.lv5.poc.library.mq.rocketmq.auth.ClientRPCHook
import moe.lv5.poc.library.mq.rocketmq.auth.SessionCredentials
import moe.lv5.poc.library.mq.rocketmq.config.DailyConfig
import org.apache.rocketmq.client.producer.DefaultMQProducer
import org.apache.rocketmq.client.producer.LocalTransactionExecuter
import org.apache.rocketmq.client.producer.LocalTransactionState
import org.apache.rocketmq.client.producer.TransactionMQProducer
import org.apache.rocketmq.common.message.Message
import org.apache.rocketmq.remoting.RPCHook
import org.apache.rocketmq.remoting.common.RemotingHelper
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.LongAdder
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.test.Ignore
import kotlin.test.Test


@Ignore
class ProducerTest {

    private val config = DailyConfig

    private fun getAclRPCHook(): RPCHook {
        return ClientRPCHook(SessionCredentials(config.ACCESS_KEY, config.SECRET_KEY))
    }

    @Test
    fun ossProducerTest() {
        val rateLimiter = RateLimiter.create(1.0)
        val map = TreeMap<Long, AtomicLong>()
        val failedCount = LongAdder()
        val sendCount = LongAdder()
        val startTimeMillis = System.currentTimeMillis()

        val threadCount = 5
        val countDownLatch = CountDownLatch(threadCount)
        try {
            for (i in 1..threadCount) {
                Thread {
                    val producer = DefaultMQProducer(config.GROUP_ID, getAclRPCHook())
                    producer.instanceName = "producer_$i"
                    producer.retryTimesWhenSendAsyncFailed = 0
                    producer.retryTimesWhenSendFailed = 0
                    producer.namesrvAddr = config.NAMESRV_ADDR
                    producer.isVipChannelEnabled = false
                    producer.start()

                    val body = Random(System.currentTimeMillis()).nextBytes(4 * 1024)

                    while (true) {
                        rateLimiter.acquire()
                        val currentTimeMillis = System.currentTimeMillis()
                        val diff = (currentTimeMillis - startTimeMillis) / 1000
                        if (diff > 60 * 60) {
                            println("Thread $i shutdown")
//                            Thread.sleep(10 * 1000)
                            countDownLatch.countDown()
                            producer.shutdown()
                            break
                        }
                        val msg = Message(
                            config.TOPIC,
                            config.TAG,
                            body
                        )
                        msg.properties

//                        producer.send(msg, object : SendCallback {
//                            override fun onSuccess(sendResult: SendResult?) {
//                                val counter = map.computeIfAbsent(currentTimeMillis / 1000) { AtomicLong() }
//                                counter.incrementAndGet()
//                            }
//
//                            override fun onException(e: Throwable?) {
//                                failedCount.increment()
//                            }
//                        }, 10 * 1000)
//                        sendCount.increment()
                        producer.sendOneway(msg)
                    }
                }.start()
            }
            countDownLatch.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
//        map.forEach { (time, counter) ->
//            println("time: ${time}s, count: ${counter.get()}")
//        }

        val max = map.values.stream().max(Comparator.comparingLong(AtomicLong::get))
        val sd = calculateSD(map.values)
        println("max: ${max.get()}, standard deviation: $sd")
        var sum = 0L
        map.forEach { t, l ->
            sum += l.get()
        }
        println("sum: $sum")
        println("send count: ${sendCount.sum()}")
        println("failed count: ${failedCount.sum()}")
    }

    fun calculateSD(collection: Collection<AtomicLong>): Double {
        var sum = 0.0
        var standardDeviation = 0.0

        for (num in collection) {
            sum += num.get()
        }

        val mean = sum / collection.size

        for (num in collection) {
            standardDeviation += (num.get() - mean).pow(2.0)
        }

        return sqrt(standardDeviation / collection.size)
    }

    @Test
    fun ossProducerTest2() {
        val producer = TransactionMQProducer(config.GROUP_ID, getAclRPCHook())
        producer.namesrvAddr = config.NAMESRV_ADDR
        producer.isVipChannelEnabled = false
        producer.setTransactionCheckListener {
            return@setTransactionCheckListener LocalTransactionState.COMMIT_MESSAGE
        }
        producer.start()
        val rateLimiter = RateLimiter.create(100.0)
        try {
            val currentTimeMillis = System.currentTimeMillis()
            while (true) {
                rateLimiter.acquire()
                val msg = Message(
                    config.TOPIC,
                    config.TAG,
                    "Hello world".toByteArray(charset(RemotingHelper.DEFAULT_CHARSET))
                )
//                msg.properties["__SHARDINGKEY"] = "100"
//                msg.properties["TRAN_MSG"] = "true"
//                msg.properties["__STARTDELIVERTIME"] = "$currentTimeMillis"
                val sendResult = producer.sendMessageInTransaction(msg, LocalTransactionExecuter { msg, arg ->
                    return@LocalTransactionExecuter LocalTransactionState.COMMIT_MESSAGE
                }, null)
//                println(sendResult)
//                Thread.sleep(100)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        producer.shutdown()
    }

    @Test
    fun aliyunProducerTest() {
        val properties = Properties()
        properties[PropertyKeyConst.AccessKey] = config.ACCESS_KEY
        properties[PropertyKeyConst.SecretKey] = config.SECRET_KEY
        properties[PropertyKeyConst.NAMESRV_ADDR] = config.NAMESRV_ADDR
        val producer = ONSFactory.createProducer(properties)
        producer.start()

        try {
            val msg = com.aliyun.openservices.ons.api.Message(
                config.TOPIC.substringAfter('%'),
                config.TAG,
                "Hello MQ".toByteArray()
            )
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

    @Test
    fun aliyunRAMProducerTest() {
        val properties = Properties()
        properties[PropertyKeyConst.RAM_ROLE_NAME] = config.RAM_ROLE_NAME
        properties[PropertyKeyConst.NAMESRV_ADDR] = config.NAMESRV_ADDR
        val producer = ONSFactory.createProducer(properties)
        producer.start()

        try {
            val msg = com.aliyun.openservices.ons.api.Message(
                config.TOPIC.substringAfter('%'),
                config.TAG,
                "Hello MQ".toByteArray()
            )
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
