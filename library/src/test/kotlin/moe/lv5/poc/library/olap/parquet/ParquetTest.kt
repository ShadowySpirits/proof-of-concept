package moe.lv5.poc.library.olap.parquet

import com.google.common.base.Stopwatch
import io.github.serpro69.kfaker.Faker
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.hadoop.fs.Path
import org.apache.parquet.hadoop.metadata.CompressionCodecName
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import java.io.IOException
import java.sql.DriverManager
import java.util.concurrent.TimeUnit
import kotlin.random.Random


@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class ParquetTest {
    private var SCHEMA: Schema? = null
    private val SCHEMA_LOCATION = "avroToParquet.avsc"
    private val OUT_PATH: Path = Path("/tmp/parquet")

    @Order(1)
    @Test
    fun testWrite() {
        javaClass.classLoader.getResourceAsStream(SCHEMA_LOCATION)?.use {
            SCHEMA = Schema.Parser().parse(it)
        }
        if (SCHEMA == null) {
            throw IOException("Schema is null")
        }

        val faker = Faker()
        val random = Random(System.currentTimeMillis())

        val recordList = mutableListOf<GenericData.Record>()
        for (i in 1..5) {
            if (i % 10000 == 0) {
                println(i * 100.0 / 5000000)
            }
            val record = GenericData.Record(SCHEMA)
            record.put("id", faker.idNumber.invalid())
            record.put("topic", "test")
            record.put("queueId", 0)
            record.put("offset", i)
            record.put("size", random.nextInt(1024, 1024 * 1024))
            record.put("timestamp", System.currentTimeMillis())
            record.put("bornHost", fakeIP(random))
            record.put("storeHost", fakeIP(random))
            record.put("tag", listOf(faker.address.country(), faker.address.country()))
            record.put("key", listOf(faker.address.city(), faker.address.city()))
            record.put(
                "property", mapOf(
                    "INSTANCE_ID" to "MQ_INST_1360758716202565_BcFGJuj8",
                    "__FQN_TOPIC" to "MQ_INST_1360758716202565_BcFGJuj8%yesoul-txtopic-converter\n",
                    "EagleEye-UserData" to "tbs=frs",
                    "EagleEye-RpcID" to "0.10",
                    "__MESSAGE_DECODED_TIME" to "${System.currentTimeMillis()}",
                    "MSG_REGION" to "cn-shanghai",
                    "id" to faker.idNumber.invalid(),
                    "EagleEye-SpanID" to "${System.currentTimeMillis()}",
                    "WAIT" to "false",
                    "courseId" to "${random.nextInt(1000000, 9999999)}",
                    "contentType" to "application/json",
                    "PGROUP" to "MQ_INST_1360758716202565_BcFGJuj8%GID-tx-yesoul-sub-tcp-converter-course-download",
                    "apm" to "{\"elastic-apm-traceparent\":\"${
                        faker.random.randomString(
                            32,
                            true
                        )
                    }\",\"traceparent\":\"${faker.random.randomString(32, true)}\"}",
                    "timestamp" to "${System.currentTimeMillis()}",
                    "EagleEye-pRpc" to "BEAN(Method)-courseDownloadHandler",
                    "__transactionId__" to faker.random.randomString(32),
                    "TRACE_ON" to "true",
                    "EagleEye-TraceID" to faker.random.randomString(32, true),
                    "EagleEye-pAppName" to "1ea39c72-2aa2-447e-adc7-346bfceaf97d",
                    "EagleEye-Sampled" to "s0",
                    "EagleEye-IP" to "172.19.46.97",
                    "EagleEye-ROOT-APP" to "1ea39c72-2aa2-447e-adc7-346bfceaf97d",
                    "EagleEye-TraceID" to faker.random.randomString(32),
                )
            )
            recordList.add(record)
        }

        val idList = listOf(
            "08D65631000E681A951542D93F169FA2",
            "C2E6376BBCF045217FE14A701EFEFB79",
            "D1F065DBBCF045217FCF4A701EFD0026",
            "AC132E61000172E295CC219C2CAC0006"
        )
        val tagList = listOf("test_tag1", "test_tag2", "test_tag3", "test_tag4")
        val keyList = listOf("test_key1", "test_key2", "test_key3", "test_key4")
        val dbList = listOf("postgres", "mysql", "oracle", "sqlserver")
        for (i in 1..24) {
            println("write file $i")
            val record = GenericData.Record(SCHEMA)
            record.put("id", idList[i % 4])
            record.put("topic", "test")
            record.put("queueId", 0)
            record.put("offset", i)
            record.put("size", random.nextInt(1024, 1024 * 1024))
            record.put("timestamp", System.currentTimeMillis())
            record.put("bornHost", "127.0.0.1")
            record.put("storeHost", "172.19.93.48")
            record.put("tag", faker.random.randomSublist(tagList, 2, true))
            record.put("key", faker.random.randomSublist(keyList, 2, true))
            record.put("property", mapOf("user" to "xuanjue", "db" to dbList[i % 4]))

            val writerZstd = Parquet.buildWriter(
                SCHEMA!!,
                Path("$OUT_PATH/sample-zstd$i.parquet"),
                CompressionCodecName.ZSTD
            )
            val stopwatchZstd = Stopwatch.createStarted()
            for (i in 1..4) {
                recordList.forEach {
                    writerZstd.write(it)
                }
            }
            writerZstd.write(record)
            val zstdDataSize = writerZstd.dataSize
            writerZstd.close()
            val writeZstdFileElapsed = stopwatchZstd.elapsed(TimeUnit.MILLISECONDS)
            println("write zstd file1 cost: ${writeZstdFileElapsed}ms")

            val writerSnappy = Parquet.buildWriter(
                SCHEMA!!,
                Path("$OUT_PATH/sample-snappy$i.parquet"),
                CompressionCodecName.SNAPPY
            )
            val stopwatchSnappy = Stopwatch.createStarted()
            for (i in 1..4) {
                recordList.forEach {
                    writerSnappy.write(it)
                }
            }
            writerSnappy.write(record)
            val snappyDataSize = writerSnappy.dataSize
            writerSnappy.close()
            val writeSnappyFileElapsed = stopwatchSnappy.elapsed(TimeUnit.MILLISECONDS)
            println("write snappy file$i cost: ${writeSnappyFileElapsed}ms")
            println("snappy/zstd time: ${writeSnappyFileElapsed * 100.0 / writeZstdFileElapsed}%")
            println("snappy/zstd size: ${snappyDataSize * 100.0 / zstdDataSize}%")
            println()
        }
//        Parquet.readFromParquet(OUT_PATH)
    }

    @Order(2)
    @Test
    fun testSQL() {
        val conn = DriverManager.getConnection("jdbc:duckdb:")
        val stmt = conn.createStatement()
//        stmt.execute("DESCRIBE SELECT * FROM '/root/sample.parquet'")
//        println(stmt.resultSet)
        val stopwatch = Stopwatch.createStarted()
        val resultSet = stmt.executeQuery(
            "SELECT topic, timestamp, to_json(tag)::TEXT AS tag, to_json(property)::TEXT AS property FROM '$OUT_PATH/*.parquet'" +
                    " WHERE list_extract(element_at(property, 'game'), 1) = 'postgres' OR list_has(tag, 'test_tag1')"
        )
        println("execution time: ${stopwatch.elapsed(TimeUnit.MILLISECONDS)}ms")
        var count = 0
        while (resultSet.next()) {
            println("item ${count++}:")
            val columnCount = resultSet.metaData.columnCount
            for (i in 1..columnCount) {
                println(resultSet.metaData.getColumnName(i) + ": " + resultSet.getObject(i))
            }
            println()
        }
        println("get result cost time: ${stopwatch.elapsed(TimeUnit.MILLISECONDS)}ms, result count: $count")
    }

    fun fakeIP(random: Random): String {
        return "${random.nextInt(1, 255)}.${random.nextInt(1, 255)}.${random.nextInt(1, 255)}.${random.nextInt(1, 255)}"
    }
}
