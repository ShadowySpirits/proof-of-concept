package orm.exposed

import net.dzikoysk.exposed.upsert.upsert
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import orm.exposed.Histories.data
import orm.exposed.Queries.aoneId
import java.time.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ExposedTest {
    init {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MYSQL",
            driver = "org.h2.Driver",
            databaseConfig = DatabaseConfig {
                sqlLogger = StdOutSqlLogger
            })

        println("setup table")
        transaction {
            SchemaUtils.create(Queries, Histories)
        }
        println()
    }

    @BeforeTest
    fun setup() {
        println("setup data")
        transaction {
            Queries.deleteAll()
            Histories.deleteAll()

            val queryEntity = QueryEntity.new {
                title = "The Last Jedi"
                aoneId = "123456"
                userId = "263665"
                type = "Rian Johnson"
                createTime = Instant.now()
            }

            HistoryEntity.new {
                data = "qwe"
                this.query = queryEntity
                createTime = Instant.now()
            }

            HistoryEntity.new {
                data = "asd"
                this.query = queryEntity
                createTime = Instant.now()
            }
        }
        println()
    }

    @Test
    fun testDeleteCascade() {
        transaction {
            // use onDelete = ReferenceOption.CASCADE in reference
            QueryEntity.find { aoneId eq "123456" }.with(QueryEntity::histories).first().delete()
            assertEquals(0, HistoryEntity.all().count())
        }
    }

    @Test
    fun testOneToMany() {
        val pair = transaction {
            // use load/with to avoid N+1 problem
            val query = QueryEntity.find { aoneId eq "123456" }.with(QueryEntity::histories).first()
            assertEquals(2, query.histories.count())
            // Blob and text fields and reference won't be available outside a transaction if you don't load them directly
            // For text fields you can also use the eagerLoading param: text("content", eagerLoading = true)
            // https://github.com/JetBrains/Exposed/wiki/Transactions#accessing-returned-values
            return@transaction query to query.histories.toList()
        }
        assertNull(pair.first.histories)
        assertEquals(2, pair.second.size)
    }

    @Test
    fun testManyToOne() {
        transaction {
            val history = HistoryEntity.find { data eq "qwe" }.first()
            assertEquals("263665", history.query.userId)
        }
    }

    @Test
    fun testUpsert() {
        // https://github.com/reposilite-playground/exposed-upsert
        transaction {
            Queries.upsert(conflictColumn = aoneId,
                insertBody = {
                    it[title] = "The Last Jedi"
                    it[aoneId] = "123456"
                    it[userId] = "110369"
                    it[type] = "Rian Johnson"
                    it[createTime] = Instant.now()
                }, updateBody = {
                    it[userId] = "110369"
                })
            assertEquals("110369", QueryEntity.find { aoneId eq "123456" }.first().userId)
            assertEquals(1, QueryEntity.count())

            Queries.upsert(conflictColumn = aoneId,
                insertBody = {
                    it[title] = "The Last Jedi"
                    it[aoneId] = "654321"
                    it[userId] = "WB502261"
                    it[type] = "Rian Johnson"
                    it[createTime] = Instant.now()
                }, updateBody = {
                    it[userId] = "WB502261"
                })
            assertEquals("WB502261", QueryEntity.find { aoneId eq "654321" }.first().userId)
            assertEquals(2, QueryEntity.count())
        }
    }
}
