plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
}

group = "org.fitnessConstructor"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("org.fitnessConstructor")
    // adding this for fatjar
        //    project.setProperty("mainClassName", "org.fitnessConstructor.ApplicationKt")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.ktor.serialization.gson)
    implementation(libs.ktor.server.host.common)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.request.validation)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)

    implementation(libs.flyway.core)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.java.time)
    implementation(libs.exposed.kotlin.datetime)

    // implementation(libs.postgresql)
    implementation(libs.mysql.connector)
    implementation(libs.hikari)

    implementation(libs.bcrypt)
    implementation(libs.kotlinx.datetime)
    implementation(libs.commons.email)
    implementation(libs.valiktor.core)
    implementation(libs.commons.io)

    implementation(libs.ktor.swagger.ui)
    implementation(libs.ktor.open.api)

    implementation(libs.koin.ktor)
    implementation(libs.koin.core)
    implementation(libs.koin.logger)

    implementation(libs.ktorm.core)
    implementation(libs.ktorm.mysql)
    implementation(libs.ktorm.jackson)
    implementation("com.tfowl.ktor:ktor-jsoup:2.3.0")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:5.6.1")
    implementation("io.arrow-kt:arrow-core:2.2.2")
    implementation("io.arrow-kt:arrow-fx-coroutines:2.2.2")

}
kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(20))
    }
}
tasks.withType<JavaCompile> {
    sourceCompatibility = "20"
    targetCompatibility = "20"
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(20))
    }
}
tasks.create("stage") {
    dependsOn("installDist")
}
tasks.shadowJar {
    archiveBaseName.set("app")
    archiveClassifier.set("")
    archiveVersion.set("")
    manifest {
        attributes["Main-Class"] = "org.fitnessConstructor.ApplicationKt"
    }
}
tasks.test {
    useJUnitPlatform()
}