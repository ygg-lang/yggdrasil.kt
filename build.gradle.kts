plugins {
    kotlin("jvm") version "1.9.0"
}

group = "yggdrasil"
version = "0.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}