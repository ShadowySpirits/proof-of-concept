import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
}

allprojects {
    group = "moe.lv5.poc"
    version = "1.0-SNAPSHOT"
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://repo.panda-lang.org/releases")
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        implementation(Kotlin.stdlib.jdk8)
        implementation(KotlinX.coroutines.core)
        implementation(kotlin("reflect"))

        testImplementation(Testing.Junit.Jupiter)
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
        useJUnitPlatform()
    }
}
