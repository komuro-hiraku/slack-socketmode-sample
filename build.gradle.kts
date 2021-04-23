import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.31"
    id("application")
}

group = "me.komurohiraku"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.slack.api:bolt-socket-mode:1.5.0")
    implementation("com.slack.api:slack-api-model-kotlin-extension:1.5.0")
    implementation("org.glassfish.tyrus.bundles:tyrus-standalone-client:1.17")
    implementation("org.slf4j:slf4j-simple:1.7.30")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
}

application {
    mainClassName = "MyAppKt" // ソースファイル名の末尾、拡張子の代わりに "Kt" をつけた命名になります
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}