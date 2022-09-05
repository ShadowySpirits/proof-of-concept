package moe.lv5.poc.vertx.service

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.jsonObjectOf
import moe.lv5.poc.vertx.annotation.Get
import moe.lv5.poc.vertx.annotation.Service
import moe.lv5.poc.vertx.base.CustomVerticle
import moe.lv5.poc.vertx.base.dao.clash.Clash
import moe.lv5.poc.vertx.extension.sendError


@Service("/clash")
class ClashVerticle : CustomVerticle() {

    private lateinit var clashConfigStore: ConfigStoreOptions
    private lateinit var retriever: ConfigRetriever

    override suspend fun start() {
        super.start()
        // http://sttsub.xyz/link/ANHc0LBJHUFqMRwR?clash=1
        clashConfigStore = ConfigStoreOptions()
            .setFormat("yaml")
            .setType("http")
            .setConfig(
                jsonObjectOf(
                    "host" to "sttsub.xyz",
                    "path" to "/link/ANHc0LBJHUFqMRwR?clash=1",
                    "followRedirects" to true
                )
            )
        retriever = ConfigRetriever.create(vertx, ConfigRetrieverOptions().also {
            it.addStore(clashConfigStore)
        })
    }

    @Get("/sttlink")
    suspend fun getSttLink(ctx: RoutingContext) {
        retriever.getConfig {
            if (it.succeeded()) {
                val result = it.result()
                ctx.response().end(it.result().encodePrettily())
            } else ctx.response().sendError(HttpResponseStatus.NOT_FOUND)
        }
    }

    @Get("/compose")
    fun compose(ctx: RoutingContext) {
        retriever.getConfig {
            if (it.succeeded()) {
                val clash = Clash(proxies = it.result().getJsonArray("proxies"))
                ctx.response().end(JsonObject.mapFrom(clash).encode())
            } else ctx.response().sendError(HttpResponseStatus.NOT_FOUND)
        }
    }
}
