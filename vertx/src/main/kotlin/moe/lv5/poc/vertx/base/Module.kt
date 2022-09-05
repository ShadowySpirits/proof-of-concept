package moe.lv5.poc.vertx.base

import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import org.koin.dsl.module

val frameworkModule = module {
    single { (vertx: Vertx) -> Router.router(vertx) }
}
