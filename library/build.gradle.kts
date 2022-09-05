dependencies {
    implementation(project(":base"))

    implementation("org.jetbrains.exposed:exposed-core:_")
    implementation("org.jetbrains.exposed:exposed-dao:_")
    implementation("org.jetbrains.exposed:exposed-jdbc:_")
    implementation("org.jetbrains.exposed:exposed-java-time:_")
    implementation("com.h2database:h2:_")

    implementation("org.apache.rocketmq:rocketmq-client:_")
    implementation("org.apache.rocketmq:rocketmq-tools:_")
    implementation("com.aliyun.openservices:ons-client:_")

    implementation("com.fasterxml.jackson.core:jackson-core:_")
    implementation("com.fasterxml.jackson.core:jackson-databind:_")

    implementation("org.rocksdb:rocksdbjni:_")

    implementation("com.github.ben-manes.caffeine:caffeine:_")
    implementation("com.google.guava:guava:_")

    implementation("org.apache.parquet:parquet-avro:_")
    implementation("org.apache.hadoop:hadoop-common:_")
    implementation("org.apache.hadoop:hadoop-mapreduce-client-core:_")
    implementation(fileTree("/Users/sspirits/.m2/repository/com/aliyun/jindodata") {
        include("*.jar")
    })

    implementation("org.duckdb:duckdb_jdbc:_")

    testImplementation("io.github.serpro69:kotlin-faker:_")

    implementation("com.github.kotlin-telegram-bot:kotlin-telegram-bot:_")
    implementation("mysql:mysql-connector-java:_")
}
