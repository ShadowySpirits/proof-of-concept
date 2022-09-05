package moe.lv5.poc.vertx.annotation

import moe.lv5.poc.vertx.base.HttpMethod

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Path(val method: HttpMethod = HttpMethod.GET, val path: String = "/")
