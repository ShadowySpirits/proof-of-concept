import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    jacoco
}

allprojects {
    group = "moe.lv5.poc"
    version = "1.0-SNAPSHOT"
    repositories {
        mavenCentral()
        mavenLocal()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "jacoco")

    dependencies {
        implementation(Kotlin.stdlib.jdk8)
        implementation(KotlinX.coroutines.core)
        implementation(kotlin("reflect"))

        testImplementation(Kotlin.test)
        testImplementation(Kotlin.test.junit5)
        testImplementation(Testing.Junit.Jupiter)
        testImplementation("org.awaitility:awaitility-kotlin:_")
    }

    sourceSets.main {
        java.srcDirs("src/main/kotlin")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    tasks.withType<Test> {
        finalizedBy(tasks.jacocoTestReport)
        useJUnitPlatform()

        testLogging {
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            events("passed", "failed", "skipped")
        }
    }

    tasks.jacocoTestReport {
        reports {
            xml.required.set(true)
            csv.required.set(true)
            html.required.set(true)
        }
    }
}
