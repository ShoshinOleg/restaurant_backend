val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm") version "1.6.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.0"
}

group = "com.shoshin"
version = "0.0.1"
application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
    google()
    maven("https://jitpack.io")
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-gson:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    implementation("com.google.firebase:firebase-admin:8.1.0")
    implementation("com.google.gms:google-services:4.3.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-RC3")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.9.3"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")


    //    def retrofitVersion = "2.9.0"
    //    implementation "com.squareup.retrofit2:retrofit:$retrofitVersion"
    //    implementation "com.squareup.retrofit2:converter-gson:$retrofitVersion"
    //    implementation "com.squareup.retrofit2:adapter-rxjava3:$retrofitVersion"
    //
    //
    //    implementation 'com.google.dagger:dagger:2.40.5'
    //    kapt 'com.google.dagger:dagger-compiler:2.40.5'
    //
    //
    //    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.9.3"))
    //    implementation("com.squareup.okhttp3:okhttp")
    //    implementation("com.squareup.okhttp3:logging-interceptor")
}

tasks.create("stage") {
    dependsOn("installDist")
}