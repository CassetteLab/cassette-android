# Repository Instructions

## Project Shape
- Kotlin Multiplatform Gradle project named `Cassette`; modules are `:androidApp`, `:desktopApp`, `:shared`, `:shared:core`, `:shared:domain`, `:shared:data`, and `:shared:presentation` from `settings.gradle.kts`.
- The project targets Android, desktop/JVM, and iOS source sets, but Android is the priority platform. When a trade-off is required, preserve Android behavior, quality, and build health first.
- Dependency direction is explicit in Gradle: `:androidApp` and `:desktopApp` depend on `:shared`; `:shared` composes `:shared:core`, `:shared:domain`, `:shared:data`, and `:shared:presentation`; `:shared:domain` depends on `:shared:core`; `:shared:data` depends on `:shared:core` and `:shared:domain`; `:shared:presentation` depends on `:shared:core` and `:shared:domain`.
- Shared code belongs in `commonMain` by default. Use platform source sets (`androidMain`, `iosMain`, `jvmMain`) only for APIs, dependencies, or behavior that cannot be implemented in common code.
- Android entrypoint is `androidApp/src/main/kotlin/fr/cassettelabs/cassette/MainActivity.kt`, launched by `androidApp/src/main/AndroidManifest.xml`.
- Shared Compose UI entrypoint is `shared/src/commonMain/kotlin/fr/cassettelabs/cassette/App.kt`; iOS exposes it through `shared/src/iosMain/kotlin/fr/cassettelabs/cassette/MainViewController.kt`; desktop starts from `desktopApp/src/main/kotlin/fr/cassettelabs/cassette/main.kt`.
- Koin modules are declared under module-specific `di/*Module.kt` files. `androidApp/src/main/kotlin/fr/cassettelabs/cassette/CassetteApplication.kt` starts Koin on Android.

## UI Work
- Before creating, editing, or reviewing Compose Multiplatform UI, screens, previews, UI state/events, or files under `shared/src/commonMain` or `shared/presentation/src/*Main`, load the `jetpack-compose-ui` skill first.
- Prefer shared Compose UI in `commonMain` when possible. Keep Android-specific UI code in `androidMain` only when required by Android APIs or tooling.

## Build And Verification
- Use the wrapper: `./gradlew ...`.
- Quick Android app build: `./gradlew :androidApp:assembleDebug`.
- Full local build/check for a module: `./gradlew :module:build`, for example `./gradlew :shared:data:build`.
- Android unit tests: `./gradlew :androidApp:testDebugUnitTest` or Android target test tasks on shared modules when available.
- Common/JVM KMP tests: use the relevant KMP test task, for example `./gradlew :shared:domain:allTests`, `./gradlew :shared:data:allTests`, or `./gradlew :shared:presentation:allTests`.
- Desktop run/build tasks live under `:desktopApp`; use them only when the change affects desktop behavior.
- Focus a single JVM/unit test with Gradle's filter when the selected task supports it, for example `./gradlew :shared:domain:jvmTest --tests 'fr.cassettelabs.cassette.ExampleTest'`.
- Android lint exists only via AGP tasks; run `./gradlew :androidApp:lint` or the relevant Android/KMP module lint task when available.
- Instrumentation tests require a connected Android device/emulator: `./gradlew :androidApp:connectedDebugAndroidTest`.

## Toolchain Notes
- Gradle daemon toolchain is pinned to Java 21 in `gradle/gradle-daemon-jvm.properties`; source/target compatibility is Java 11.
- Android configuration uses versions from `gradle/libs.versions.toml`; currently `compileSdk`/`targetSdk` are 36 and `minSdk` is 24.
- Dependencies and plugin versions are centralized in `gradle/libs.versions.toml`; add libraries there before using aliases in module Gradle files.
- KMP dependencies should be added to the narrowest appropriate source set (`commonMain`, `androidMain`, `iosMain`, or `jvmMain`). Prefer `commonMain` only for dependencies that are truly multiplatform.
- There is no repo-local ktlint, detekt, formatter, CI, or pre-commit config at the time this file was written.
