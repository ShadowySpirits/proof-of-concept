package moe.lv5.poc.library.telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton.CallbackData
import com.github.kotlintelegrambot.extensions.filters.Filter
import com.github.kotlintelegrambot.logging.LogLevel
import com.github.kotlintelegrambot.types.TelegramBotResult
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

object Bills : IntIdTable("ugc_cmb_bill") {
    val tailNum = integer("tail_num")
    val cardType = varchar("card_type", 45)
    val billType = varchar("bill_type", 45)
    val currency = varchar("currency", 45)
    val money = double("money")
    val balance = double("balance")
    val raw = varchar("raw", 1024)
    val category = varchar("category", 45)
    val detail = varchar("detail", 1024)
    val counterParty = varchar("counter_party", 45)
    val tag = varchar("tag", 256)
    val time = timestamp("time")
}

class BillEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BillEntity>(Bills)

    var tailNum by Bills.tailNum
    var cardType by Bills.cardType
    var billType by Bills.billType
    var currency by Bills.currency
    var money by Bills.money
    var balance by Bills.balance
    var raw by Bills.raw
    var category by Bills.category
    var detail by Bills.detail
    var counterParty by Bills.counterParty
    var tag by Bills.tag
    var time by Bills.time

    override fun toString(): String {
        return "$detail\n" +
                "${formatter.format(time)}\n" +
                "$cardType $tailNum $billType $currency $money\n" +
                "交易类别：$category\n" +
                "交易对象：$counterParty\n" +
                "余额：$balance\n" +
                "\n$raw\n\n" +
                "#${
                    tag.replace(
                        ",",
                        " #"
                    )
                }"
    }
}

lateinit var bot: Bot

val idRegex = Regex("id:\\s(\\d+)")
val tagRegex = Regex("#([^#\\s]+)")

val chatOperationMap = mutableMapOf<Long, Triple<String, Int, MutableList<Long>>>()

val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
    .withLocale(Locale.CHINA)
    .withZone(ZoneId.systemDefault());

fun getIdFromMsg(msg: String?): Int? {
    if (msg == null || !msg.contains("插入数据库成功")) {
        return null
    }
    idRegex.find(msg)?.apply {
        if (groupValues.isNotEmpty()) {
            try {
                return groupValues[1].toInt()
            } catch (ignore: NumberFormatException) {
            }
        }
    }
    return null
}

fun findBillByMsg(msg: String): BillEntity? {
    idRegex.find(msg)?.apply {
        if (groupValues.isNotEmpty()) {
            val id = groupValues[1].toInt()
            return transaction {
                BillEntity.findById(id)
            }
        }
    }
    return null
}

fun buildMainReplayMark(id: Int): InlineKeyboardMarkup {
    return InlineKeyboardMarkup.create(
        listOf(
            listOf(
                CallbackData("设置收支类型", "billType#$id"),
                CallbackData("设置交易类别", "category#$id"),
            ),
            listOf(
                CallbackData("设置交易对象", "counterParty#$id"),
                CallbackData("设置描述", "detail#$id"),
            ),
            listOf(
                CallbackData("添加标签", "addTag#$id"),
                CallbackData("删除标签", "deleteTag#$id"),
            ),
            listOf(
                CallbackData("自定义修改", "diy#$id"),
            ),
        )
    )
}

fun changeBillData(type: String, id: Int, data: String, messageIdList: List<Long>) {
    transaction {
        BillEntity.findById(id)?.let { bill ->
            when (type) {
                "billType" -> bill.billType = data.trim()
                "category" -> bill.category = data.trim()
                "counterParty" -> bill.counterParty = data.trim()
                "detail" -> bill.detail = data.trim()
                "addTag" -> {
                    val tagSet = bill.tag.split(",").toMutableSet()
                    data.trim().split(" ", "\n", "\r").forEach {
                        tagSet.add(it.trim())
                    }
                    bill.tag = tagSet.joinToString(",")
                }
                "deleteTag" -> {
                    val tagSet = bill.tag.split(",").toMutableSet()
                    data.trim().split(" ", "\n", "\r").forEach {
                        tagSet.remove(it.trim())
                    }
                    bill.tag = tagSet.joinToString(",")
                }
            }
            val result = bot.sendMessage(
                ChatId.fromId(-584441459),
                text = "成功修改交易：$bill",
                replyMarkup = buildMainReplayMark(id)
            )
            if (result.isSuccess) {
                chatOperationMap.remove(-584441459)
                messageIdList.forEach {
                    bot.deleteMessage(ChatId.fromId(-584441459), it)
                }
            }
        }
    }
}

fun dealWithDBInsert(text: String, bill: BillEntity) {
    when {
        text.contains("支付宝-支付宝-理财") || text.contains("蚂蚁基金") -> {
            bill.billType = "转账"
            bill.category = "理财"
            bill.counterParty = "支付宝理财"
            bill.tag = "理财"
            if (bill.money == 100.0) {
                bill.detail = "稳健基金定投"
            }
        }
        text.contains("轨道交通") -> {
            bill.billType = "支出"
            bill.category = "日常出行"
            bill.detail = "地铁"
            bill.counterParty = "地铁"
            bill.tag = "地铁"
        }
        else -> {
            bill.detail = "识别失败"
        }
    }
}

fun main() {
    Database.connect("jdbc:mysql://pve:3306/n8n",
        user = "",
        password = "",
        databaseConfig = DatabaseConfig {
            sqlLogger = StdOutSqlLogger
        })

    bot = bot {
        token = ""
        logLevel = LogLevel.Error
        dispatch {
            message(Filter.Chat(-584441459)) {
                if (message.from?.username == "sspirits_notification_bot") {
//                    bot.sendMessage(ChatId.fromId(message.chat.id), text = "receive message from notify bot ...")
                }

                if (message.text == null) {
                    return@message
                }
                val text = message.text!!

                if (text == "cancel" || text == "取消") {
                    val messageIdList = chatOperationMap[-584441459]!!.third
                    chatOperationMap.remove(-584441459)
                    bot.deleteMessage(ChatId.fromId(-584441459), message.messageId)
                    bot.deleteMessage(ChatId.fromId(-584441459), messageIdList[messageIdList.size - 1])
                    return@message
                }

                if (chatOperationMap.containsKey(-584441459)) {
                    val triple = chatOperationMap[-584441459]!!
                    val type = triple.first
                    val id = triple.second
                    val messageIdList = triple.third
                    messageIdList.add(message.messageId)

                    if (type == "diy") {
                        transaction {
                            BillEntity.findById(id)?.let { bill ->
                                text.split("\n").forEach {
                                    when {
                                        it.startsWith("cp") -> bill.counterParty = it.trim().substring(2).trim()
                                        it.startsWith("c") -> bill.category = it.trim().substring(1).trim()
                                        it.startsWith("d") -> bill.detail = it.trim().substring(1).trim()
                                        it.startsWith("tag") -> {
                                            bill.tag = it.trim().split(" ")
                                                .asSequence()
                                                .filter { tag ->
                                                    tag.isNotBlank() && tag != "tag"
                                                }.toList().joinToString(",")
                                        }
                                        it.startsWith("t") -> bill.billType = it.trim().substring(1)
                                    }
                                }
                                val result = bot.sendMessage(
                                    ChatId.fromId(-584441459),
                                    text = "成功修改交易：$bill",
                                    replyMarkup = buildMainReplayMark(id)
                                )
                                if (result.isSuccess) {
                                    chatOperationMap.remove(-584441459)
                                    messageIdList.forEach {
                                        bot.deleteMessage(ChatId.fromId(-584441459), it)
                                    }
                                }
                            }
                        }
                        return@message
                    }

                    changeBillData(type, id, text, messageIdList)
                    return@message
                }

                getIdFromMsg(text)?.let { id ->
                    transaction {
                        BillEntity.findById(id)?.let { bill ->
                            dealWithDBInsert(text, bill)
                            val result = bot.sendMessage(
                                ChatId.fromId(message.chat.id),
                                text = "#银行卡账单\n自动识别交易：$bill",
                                replyMarkup = buildMainReplayMark(id)
                            )
                            if (result.isSuccess) {
                                bot.deleteMessage(ChatId.fromId(message.chat.id), message.messageId)
                            }
                        }
                    }
                }
            }
            callbackQuery {
                val dataList = callbackQuery.data.split("#")
                val type = dataList[0]

                if (dataList.size == 1) {
                    when (type) {
                        "餐饮" -> {
                            bot.editMessageText(
                                ChatId.fromId(callbackQuery.message!!.chat.id),
                                text = "请输入交易类别",
                                messageId = callbackQuery.message!!.messageId,
                                replyMarkup = InlineKeyboardMarkup.createSingleRowKeyboard(
                                    CallbackData("餐饮", "餐饮~"),
                                    CallbackData("正餐", "正餐"),
                                    CallbackData("夜宵", "夜宵"),
                                    CallbackData("零食", "零食"),
                                    CallbackData("水果", "水果"),
                                    CallbackData("饮料", "饮料")
                                )
                            )
                            return@callbackQuery
                        }
                        "购物" -> {
                            bot.editMessageText(
                                ChatId.fromId(callbackQuery.message!!.chat.id),
                                text = "请输入交易类别",
                                messageId = callbackQuery.message!!.messageId,
                                replyMarkup = InlineKeyboardMarkup.createSingleRowKeyboard(
                                    CallbackData("购物", "购物~"),
                                    CallbackData("衣物", "衣物"),
                                    CallbackData("生活用品", "生活用品"),
                                    CallbackData("电子产品", "电子产品"),
                                    CallbackData("宠物", "宠物")
                                )
                            )
                            return@callbackQuery
                        }
                        "日常" -> {
                            bot.editMessageText(
                                ChatId.fromId(callbackQuery.message!!.chat.id),
                                text = "请输入交易类别",
                                messageId = callbackQuery.message!!.messageId,
                                replyMarkup = InlineKeyboardMarkup.createSingleRowKeyboard(
                                    CallbackData("日常", "日常~"),
                                    CallbackData("房租", "房租"),
                                    CallbackData("话费", "话费"),
                                    CallbackData("水电费", "水电费"),
                                    CallbackData("日常出行", "日常出行"),
                                    CallbackData("差旅费", "差旅费")
                                )
                            )
                            return@callbackQuery
                        }
                        "娱乐" -> {
                            bot.editMessageText(
                                ChatId.fromId(callbackQuery.message!!.chat.id),
                                text = "请输入交易类别",
                                messageId = callbackQuery.message!!.messageId,
                                replyMarkup = InlineKeyboardMarkup.createSingleRowKeyboard(
                                    CallbackData("娱乐", "娱乐~"),
                                    CallbackData("游戏", "游戏")
                                )
                            )
                            return@callbackQuery
                        }
                    }

                    if (chatOperationMap.containsKey(-584441459)) {
                        val triple = chatOperationMap[-584441459]!!
                        val type = triple.first
                        val id = triple.second
                        val messageIdList = triple.third

                        changeBillData(type, id, callbackQuery.data.replace("~", ""), messageIdList)
                    }
                    return@callbackQuery
                }

                val id = try {
                    dataList[1].toInt()
                } catch (e: NumberFormatException) {
                    return@callbackQuery
                }
                val messageIdList = mutableListOf<Long>()
                messageIdList.add(callbackQuery.message!!.messageId)
                chatOperationMap[callbackQuery.message!!.chat.id] = Triple(type, id, messageIdList)
                var result: TelegramBotResult<Message>? = null
                when (type) {
                    "billType" -> result = bot.sendMessage(
                        ChatId.fromId(callbackQuery.message!!.chat.id),
                        text = "请输入收支类型",
                        replyMarkup = InlineKeyboardMarkup.createSingleRowKeyboard(
                            CallbackData("支出", "支出"),
                            CallbackData("收入", "收入"),
                            CallbackData("转账", "转账")
                        )
                    )
                    "category" -> result = bot.sendMessage(
                        ChatId.fromId(callbackQuery.message!!.chat.id),
                        text = "请输入交易类别",
                        replyMarkup = InlineKeyboardMarkup.createSingleRowKeyboard(
                            CallbackData("餐饮", "餐饮"),
                            CallbackData("购物", "购物"),
                            CallbackData("日常", "日常"),
                            CallbackData("教育", "教育"),
                            CallbackData("礼物", "礼物"),
                            CallbackData("娱乐", "娱乐")
                        )
                    )
                    "counterParty" -> result =
                        bot.sendMessage(ChatId.fromId(callbackQuery.message!!.chat.id), text = "请输入交易对象")
                    "detail" -> result = bot.sendMessage(ChatId.fromId(callbackQuery.message!!.chat.id), text = "请输入描述")
                    "addTag" -> result =
                        bot.sendMessage(ChatId.fromId(callbackQuery.message!!.chat.id), text = "请输入要添加的标签，用空格或换行符分割")
                    "deleteTag" -> result =
                        bot.sendMessage(ChatId.fromId(callbackQuery.message!!.chat.id), text = "请输入要删除的标签，用空格或换行符分割")
                    "diy" -> result = bot.sendMessage(
                        ChatId.fromId(callbackQuery.message!!.chat.id),
                        text = "请输入要修改的内容\nt：收支类型\nc：交易类别\ncp：交易对象\nd：描述\ntag：标签"
                    )
                }
                if (result?.isSuccess == true) {
                    messageIdList.add(result.get().messageId)
                }
            }
            message(Filter.Reply) {
                getIdFromMsg(message.replyToMessage?.text)?.let { id ->
                    transaction {
                        BillEntity.findById(id)?.let { bill ->
                            val resultSequence = tagRegex.findAll(message.text!!)
                            val tag = resultSequence.map { result ->
                                if (result.groups.isNotEmpty()) {
                                    return@map result.groupValues[1]
                                }
                                return@map null
                            }.filterNotNull().toList().joinToString(",")
                            bill.tag = tag
                            bot.sendMessage(ChatId.fromId(message.chat.id), text = "add tag for $: $id")
                        }
                    }
                }
            }
            command("start") {
                val result = bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = "Hi there!")
                result.fold({
                    // do something here with the response
                    println(it)
                }, {
                    // do something with the error
                    println(it)
                })
            }
        }
    }
    bot.startPolling()
}
