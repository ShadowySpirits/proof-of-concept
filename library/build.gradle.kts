dependencies {
    implementation(project(":base"))

    implementation("org.jetbrains.exposed:exposed-core:_")
    implementation("org.jetbrains.exposed:exposed-dao:_")
    implementation("org.jetbrains.exposed:exposed-jdbc:_")
    implementation("org.jetbrains.exposed:exposed-java-time:_")
    implementation("com.h2database:h2:_")

    implementation("org.apache.rocketmq:rocketmq-client:_")
    implementation("com.aliyun.openservices:ons-client:_")
}
