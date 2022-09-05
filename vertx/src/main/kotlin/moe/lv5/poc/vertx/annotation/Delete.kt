package moe.lv5.poc.vertx.annotation

import moe.lv5.poc.vertx.base.HttpMethod

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Path(method = HttpMethod.DELETE)
annotation class Delete(val path: String = "/")
