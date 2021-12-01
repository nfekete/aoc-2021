import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
    application
}

group = "me.nfekete.adventofcode"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val kotestVersion = "4.3.2"

dependencies {
    implementation("org.junit.jupiter:junit-jupiter:5.4.2")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    testImplementation ("io.kotest:kotest-runner-junit5:${kotestVersion}")
    testImplementation ("io.kotest:kotest-assertions-core:${kotestVersion}")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "15"
}

application {
    mainClassName = if (project.hasProperty("mainClass")) project.properties.get("mainClass").toString() else "NULL"
}
