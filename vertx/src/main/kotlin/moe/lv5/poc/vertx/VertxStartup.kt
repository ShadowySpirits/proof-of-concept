package moe.lv5.poc.vertx

import io.vertx.core.Launcher

fun main() {
    Launcher.executeCommand("run", MainApplication::class.java.name)
}
