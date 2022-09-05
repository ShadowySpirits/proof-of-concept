package moe.lv5.poc.vertx.service

import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpHeaderValues
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import moe.lv5.poc.vertx.annotation.Get
import moe.lv5.poc.vertx.annotation.Path
import moe.lv5.poc.vertx.annotation.Post
import moe.lv5.poc.vertx.annotation.Service
import moe.lv5.poc.vertx.base.CustomVerticle
import moe.lv5.poc.vertx.base.HttpMethod
import moe.lv5.poc.vertx.extension.sendError

@Service("/schema")
class RestVerticle : CustomVerticle() {

    @Path(HttpMethod.GET, "/all")
    fun getAllSchemas(ctx: RoutingContext) {
        val allSchemasJson = json {
            obj(

            )
        }
        ctx.response()
            .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            .end(allSchemasJson.encode())
    }

    @Get("/:id")
    fun getSchema(ctx: RoutingContext) {
        val schemaJson = JsonObject()
        ctx.addEndHandler {
            it.result()
        }

        ctx.response()
            .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            .end(schemaJson.encode())
    }

    @Post("/")
    fun postSchema(ctx: RoutingContext) {
        val schemaJson = JsonObject()
        schemaJson.let {
            ctx.response()
                .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                .end(it.encode())
        } ?: ctx.response().sendError(HttpResponseStatus.BAD_REQUEST)
    }
}
