package moe.lv5.poc.library.olap.parquet

import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import org.apache.parquet.avro.AvroParquetReader
import org.apache.parquet.avro.AvroParquetWriter
import org.apache.parquet.hadoop.ParquetReader
import org.apache.parquet.hadoop.ParquetWriter
import org.apache.parquet.hadoop.metadata.CompressionCodecName
import org.apache.parquet.hadoop.util.HadoopInputFile
import org.apache.parquet.hadoop.util.HadoopOutputFile


class Parquet {
    companion object {
        private val CONF = Configuration().also {
//            it["fs.AbstractFileSystem.oss.impl"] = "com.aliyun.jindodata.oss.OSS"
//            it["fs.oss.impl"] = ""
//            it["fs.oss.endpoint"] = ""
//            it["fs.oss.accessKeyId"] = ""
//            it["fs.oss.accessKeySecret"] = ""
//            it["dfs.support.append"] = "true"
        }

        fun buildReader(filePathToRead: Path): ParquetReader<GenericData.Record> {
            val fileSystem = FileSystem.get(CONF)
            val file = fileSystem.openFile(filePathToRead).build().join()

            return AvroParquetReader.builder<GenericData.Record>(HadoopInputFile.fromPath(filePathToRead, CONF))
                .withConf(CONF)
                .build()
        }

        fun buildWriter(
            schema: Schema,
            fileToWrite: Path,
            codec: CompressionCodecName
        ): ParquetWriter<GenericData.Record> {
            return AvroParquetWriter.builder<GenericData.Record>(HadoopOutputFile.fromPath(fileToWrite, CONF))
                .withSchema(schema)
                .withConf(CONF)
                .withBloomFilterEnabled(true)
                .withCompressionCodec(codec)
                .build()
        }
    }
}
