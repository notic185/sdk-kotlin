plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "1.9.25"
}

group = "io.github.notic185"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.slf4j:slf4j-api:2.0.16")
    testImplementation("org.slf4j:slf4j-simple:2.0.16")
    compileOnly("jakarta.servlet:jakarta.servlet-api:5.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.14")
    testImplementation("jakarta.servlet:jakarta.servlet-api:5.0.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:5.0.0-alpha.14")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
}

tasks.test {
    useJUnitPlatform()
}

java {
    withSourcesJar()
    withJavadocJar()
}

kotlin {
    jvmToolchain(8)
}
