package moe.lv5.poc.library.orm.exposed

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.timestamp

object Queries : IntIdTable() {
    val title = varchar("title", 1024)
    val userId = varchar("userId", 256)
    val aoneId = varchar("aoneId", 256).uniqueIndex()
    val type = varchar("type", 256)
    val createTime = timestamp("createTime")
}

class QueryEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<QueryEntity>(Queries)

    var title by Queries.title
    var aoneId by Queries.aoneId
    var userId by Queries.userId
    var type by Queries.type
    var createTime by Queries.createTime
    val histories by HistoryEntity referrersOn Histories.queryId
}

object Histories : IntIdTable() {
    val data = text("data", eagerLoading = true)
    val createTime = timestamp("createTime")
    val queryId = reference("query_id", Queries, onDelete = ReferenceOption.CASCADE)
}

class HistoryEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<HistoryEntity>(Histories)

    var data by Histories.data
    var createTime by Histories.createTime
    var query by QueryEntity referencedOn Histories.queryId
}
