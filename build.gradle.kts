plugins {
    application
    kotlin("jvm") version "1.4.32"
}

group = "dev.gomelody"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://schlaubi.jfrog.io/artifactory/lavakord")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha5")
    implementation("dev.kord:kord-core:0.7.0-RC3")
    implementation("io.insert-koin:koin-core:3.0.1")
    implementation("io.insert-koin:koin-core-ext:3.0.1")
    implementation("dev.schlaubi.lavakord:kord:1.0.0-SNAPSHOT")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "15"
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn" + "-Xopt-in=io.ktor.locations.KtorExperimentalLocationsAPI"
            useIR = true
        }
    }
}

kotlin {
    explicitApi()
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")