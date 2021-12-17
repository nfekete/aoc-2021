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

val kotestVersion = "4.6.3"

dependencies {
    implementation("org.junit.jupiter:junit-jupiter:5.8.2")
    implementation(kotlin("test-junit5"))
    implementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "15"
}

application {
    mainClassName = if (project.hasProperty("mainClass")) project.properties.get("mainClass").toString() else "NULL"
}
