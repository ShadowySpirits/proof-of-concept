package moe.lv5.poc.library.db.rocksdb

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DBTest {

    @BeforeTest
    fun openDB() {
        val dbFile = File("/tmp/test")
        if (dbFile.exists()) {
            dbFile.deleteRecursively()
        }
        DB.open("/tmp/test")
    }

    @AfterTest
    fun cleanDB() {
        DB.close()
    }

    @Test
    fun getTest() {
        DB.put(RocksDBColumnFamily.INDEX, "/1".encodeToByteArray(), "1".encodeToByteArray())
        DB.put(RocksDBColumnFamily.DATA, "/1".encodeToByteArray(), "2".encodeToByteArray())

        assertEquals("1", DB.get(RocksDBColumnFamily.INDEX, "/1".encodeToByteArray())?.decodeToString())
        assertEquals("2", DB.get(RocksDBColumnFamily.DATA, "/1".encodeToByteArray())?.decodeToString())
    }

    @Test
    fun writeBatchTest() {
        DB.writeBatch {
            it.put(RocksDBColumnFamily.INDEX.columnFamilyHandler, "/1".encodeToByteArray(), "1".encodeToByteArray())
            it.put(RocksDBColumnFamily.DATA.columnFamilyHandler, "/1".encodeToByteArray(), "2".encodeToByteArray())
            return@writeBatch true
        }
        assertEquals("1", DB.get(RocksDBColumnFamily.INDEX, "/1".encodeToByteArray())?.decodeToString())
        assertEquals("2", DB.get(RocksDBColumnFamily.DATA, "/1".encodeToByteArray())?.decodeToString())

        DB.writeBatch {
            it.delete(RocksDBColumnFamily.INDEX.columnFamilyHandler, "/1".encodeToByteArray())
            it.delete(RocksDBColumnFamily.DATA.columnFamilyHandler, "/1".encodeToByteArray())
            return@writeBatch true
        }
        assertNull(DB.get(RocksDBColumnFamily.INDEX, "/1".encodeToByteArray()))
        assertNull(DB.get(RocksDBColumnFamily.DATA, "/1".encodeToByteArray()))
    }

    @Test
    fun iterateTest() {
        DB.put(RocksDBColumnFamily.DATA, "/1/1".encodeToByteArray(), "1".encodeToByteArray())
        DB.put(RocksDBColumnFamily.DATA, "/2/2".encodeToByteArray(), "2".encodeToByteArray())
        DB.put(RocksDBColumnFamily.DATA, "/3/3".encodeToByteArray(), "3".encodeToByteArray())
        DB.put(RocksDBColumnFamily.DATA, "/4/4".encodeToByteArray(), "4".encodeToByteArray())
        DB.put(RocksDBColumnFamily.DATA, "/5/5".encodeToByteArray(), "5".encodeToByteArray())

        DB.iterate(RocksDBColumnFamily.DATA, "/3/") { key, value ->
            assertEquals("/3/3", key.decodeToString())
            assertEquals("3", value.decodeToString())
        }
    }
}
