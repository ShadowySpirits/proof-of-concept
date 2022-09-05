package moe.lv5.poc.library.db.rocksdb

import org.rocksdb.ColumnFamilyDescriptor
import org.rocksdb.ColumnFamilyHandle

enum class RocksDBColumnFamily(val columnFamily: String) {
    // must open RocksDB before use
    INDEX("INDEX"),
    DATA("DATA");

    lateinit var columnFamilyDescriptor: ColumnFamilyDescriptor
        internal set
    lateinit var columnFamilyHandler: ColumnFamilyHandle
        internal set
}
