plugins {
    `application`
    kotlin("jvm") version "1.8.0"
    id("com.github.johnrengelman.shadow") version "8.1.0"
}

group = "ru.spliterash"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.6")
    implementation("org.slf4j:slf4j-simple:2.0.6")
    implementation("org.apache.ftpserver:ftpserver-core:1.2.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("commons-io:commons-io:2.11.0")
}
application {
    mainClass.set("ru.spliterash.simpleftp.MainKt")
}

tasks.assemble { dependsOn(tasks.shadowJar) }