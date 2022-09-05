package moe.lv5.poc.vertx.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Service(val mountPoint: String = "/")
