package moe.lv5.poc.vertx.annotation

import moe.lv5.poc.vertx.base.HttpMethod

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Path(method = HttpMethod.PUT)
annotation class Put(val path: String = "/")
