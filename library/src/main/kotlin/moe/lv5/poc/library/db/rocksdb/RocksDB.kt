package moe.lv5.poc.library.db.rocksdb

import org.rocksdb.*
import java.io.File

object DB {
    internal val columnFamilyOptions = ColumnFamilyOptions().optimizeForSmallDb()
    internal val dbOptions = DBOptions().setCreateIfMissing(true).setCreateMissingColumnFamilies(true)
    internal val options = Options(dbOptions, columnFamilyOptions)

    lateinit var rocksDB: RocksDB private set

    fun open(path: String) {
        val dbFile = File(path)
        val columnFamilyNames: MutableList<ByteArray> = mutableListOf()
        val columnFamilyDescriptors: MutableMap<String, ColumnFamilyDescriptor> = mutableMapOf()
        val columnFamilyHandles: List<ColumnFamilyHandle> = mutableListOf()
        if (dbFile.exists()) {
            columnFamilyNames.addAll(RocksDB.listColumnFamilies(options, path))
        } else {
            columnFamilyNames.add(RocksDB.DEFAULT_COLUMN_FAMILY)
        }
        for (columnFamilyName in columnFamilyNames) {
            columnFamilyDescriptors[String(columnFamilyName, Charsets.UTF_8)] =
                ColumnFamilyDescriptor(columnFamilyName, columnFamilyOptions)
        }
        rocksDB = RocksDB.open(dbOptions, path, columnFamilyDescriptors.values.toList(), columnFamilyHandles)

        val familyHandleMap = columnFamilyHandles.associateBy { String(it.name, Charsets.UTF_8) }
        RocksDBColumnFamily.values().forEach {
            if (columnFamilyDescriptors.contains(it.columnFamily)) {
                it.columnFamilyDescriptor = columnFamilyDescriptors[it.columnFamily]!!
                it.columnFamilyHandler = familyHandleMap[it.columnFamily]!!
            } else {
                it.columnFamilyDescriptor =
                    ColumnFamilyDescriptor(it.columnFamily.toByteArray(Charsets.UTF_8), columnFamilyOptions)
                it.columnFamilyHandler = rocksDB.createColumnFamily(it.columnFamilyDescriptor)
            }
        }
    }

    fun check() {
        if (!::rocksDB.isInitialized) {
            throw IllegalStateException("DB is not opened")
        }
    }

    fun close() {
        RocksDBColumnFamily.values().forEach {
            it.columnFamilyHandler.close()
        }
        rocksDB.close()
    }

    fun get(key: ByteArray): ByteArray? {
        check()
        return rocksDB.get(key)
    }

    fun get(columnFamily: RocksDBColumnFamily, key: ByteArray): ByteArray? {
        check()
        return rocksDB.get(columnFamily.columnFamilyHandler, key)
    }

    fun put(key: ByteArray, value: ByteArray) {
        check()
        rocksDB.put(key, value)
    }

    fun put(columnFamily: RocksDBColumnFamily, key: ByteArray, value: ByteArray) {
        check()
        rocksDB.put(columnFamily.columnFamilyHandler, key, value)
    }

    fun writeBatch(batchBuilder: (t: WriteBatch) -> Boolean) {
        writeBatch(false, batchBuilder)
    }

    fun writeBatch(sync: Boolean, batchBuilder: (t: WriteBatch) -> Boolean): Boolean {
        check()
        val writeOptions = WriteOptions()
        writeOptions.setSync(sync)
        val writeBatch = WriteBatch()
        writeBatch.use {
            if (batchBuilder(writeBatch)) {
                rocksDB.write(writeOptions, writeBatch)
                return false
            }
        }
        return false
    }

    fun iterate(columnFamily: RocksDBColumnFamily, prefix: String, read: (key: ByteArray, value: ByteArray) -> Unit) {
        check()
        rocksDB.newIterator(columnFamily.columnFamilyHandler).use { iterator ->
            iterator.seek(prefix.toByteArray())
            while (iterator.isValid && iterator.key().decodeToString().startsWith(prefix)) {
                read(iterator.key(), iterator.value())
                iterator.next()
            }
            iterator.status()
        }
    }
}

fun main() {
    DB.open("/tmp/test")
    DB.put(RocksDBColumnFamily.DATA, "/1/1".encodeToByteArray(), byteArrayOf(1))
    DB.put(RocksDBColumnFamily.DATA, "/2/2".encodeToByteArray(), byteArrayOf(2))
    DB.put(RocksDBColumnFamily.DATA, "/3/3".encodeToByteArray(), byteArrayOf(3))
    DB.put(RocksDBColumnFamily.DATA, "/4/4".encodeToByteArray(), byteArrayOf(4))
    DB.put(RocksDBColumnFamily.DATA, "/5/5".encodeToByteArray(), byteArrayOf(5))
    DB.put(RocksDBColumnFamily.DATA, "/6/6".encodeToByteArray(), byteArrayOf(6))
    DB.put(RocksDBColumnFamily.DATA, "/7/7".encodeToByteArray(), byteArrayOf(7))

    DB.iterate(RocksDBColumnFamily.DATA, "/3/") { key, value ->
        println("${String(key)} -> ${String(value)}")
    }

    DB.close()
}
