//import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("app.cash.sqldelight")
}

group = "wang.mycroft"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(libs.compose.full)
    implementation(libs.material3)
    implementation(libs.bundles.slf4j)

    implementation(libs.kotlin.stdlib)
    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.coil)
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.koin)
    implementation(libs.kotlin.serialization)
    implementation(libs.kotlin.datetime)
    implementation(libs.bundles.sqldelight)
//    implementation(libs.bundles.navigation)
    implementation(libs.bundles.voyager)
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
//            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "disney-sample"
            packageVersion = "1.0.0"
        }
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("wang.mycroft.disney.data")
        }
    }
}
