plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
}

kotlin {
    iosArm64()
    iosSimulatorArm64()
    jvm()
    android {
        namespace = "fr.cassettelabs.cassette.shared.core"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.core.fileprovider)
        }
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kermit)
            implementation(libs.kermit.io)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
