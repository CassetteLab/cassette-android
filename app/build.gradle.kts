plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "fr.cassette.cassette"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "fr.cassette.cassette"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":presentation"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Koin (Dependency Injection)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
}
