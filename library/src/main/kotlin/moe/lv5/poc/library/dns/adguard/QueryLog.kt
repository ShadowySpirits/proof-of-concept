package moe.lv5.poc.library.dns.adguard

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import java.io.File
import java.time.OffsetDateTime

class QueryLog {
    fun loadQueryLogFromFile(): Flow<String> {
        val fileName = "/Users/sspirits/Downloads/querylog.json"
        return File(fileName).readLines().asFlow()
    }

    suspend fun parseQueryLog(): List<Triple<OffsetDateTime, String, Boolean>> {
        val logFlow = loadQueryLogFromFile()
        return logFlow.map {
            val mapper = ObjectMapper()
            val rootNode: JsonNode = mapper.readTree(it)

            return@map Triple(
                OffsetDateTime.parse(rootNode["T"].textValue()),
                rootNode["QH"].textValue(),
                if (rootNode["Result"].isEmpty) true
                else if (rootNode["Result"]["IsFiltered"] != null
                    && rootNode["Result"]["IsFiltered"].isBoolean
                ) !rootNode["Result"]["IsFiltered"].booleanValue()
                else true
            )
        }
            .toList()
            .sortedBy { it.first }
    }
}

suspend fun main() {
    QueryLog().parseQueryLog()
        .filter { it.first.isAfter(OffsetDateTime.parse("2022-05-25T09:20:00+08:00")) && !it.second.contains("qq.com") && !it.third }
        .distinctBy { it.second }
        .forEach {
            println(it)
        }
}
