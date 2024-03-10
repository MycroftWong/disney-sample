import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
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
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation(libs.compose.full)
    implementation(libs.material3)
    implementation(libs.bundles.slf4j)

    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.coil)
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.koin)
    implementation(libs.kotlin.serialization)
    implementation(libs.bundles.sqldelight)
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
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
