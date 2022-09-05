val log4j2Version by extra("2.17.2")
val koinVersion by extra("3.2.0")

plugins {
    id("io.vertx.vertx-plugin") version "1.3.0"
}

vertx {
    mainVerticle = "moe.lv5.poc.vertx.MainApplication"
    debugSuspend = true
}

dependencies {
    implementation("io.vertx:vertx-web:_")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:_")
    implementation("io.vertx:vertx-lang-kotlin:_")
    implementation("io.vertx:vertx-config:_")
    implementation("io.vertx:vertx-config-yaml:_")
    implementation("io.vertx:vertx-web-client:_")

    implementation("org.apache.logging.log4j:log4j-slf4j18-impl:_")
    implementation("org.apache.commons:commons-lang3:_")
    implementation("io.insert-koin:koin-core:_")
    implementation("io.insert-koin:koin-logger-slf4j:_")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")
    implementation("com.charleskorn.kaml:kaml:_")

    testImplementation("io.vertx:vertx-junit5:_")
    testImplementation("io.vertx:vertx-unit:_")
}
