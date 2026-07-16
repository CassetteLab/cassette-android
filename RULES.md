# Repository Instructions

## Project Shape
- Android/Kotlin Gradle project named `Cassette`; modules are `:app`, `:core`, `:domain`, `:data`, and `:presentation` from `settings.gradle.kts`.
- Dependency direction is explicit in Gradle: `:app` depends on all modules; `:data` depends on `:core` and `:domain`; `:presentation` depends on `:core` and `:domain`; `:domain` depends only on Koin.
- `:domain` is a Kotlin/JVM module; `:core`, `:data`, `:presentation`, and `:app` are Android modules.
- UI entrypoint is `presentation/src/main/java/fr/cassette/cassette/presentation/MainActivity.kt`, launched by `app/src/main/AndroidManifest.xml`.
- Koin modules are declared per module under `*/di/*Module.kt`; `CassetteApplication` starts Koin.

## UI Work
- Before creating, editing, or reviewing Jetpack Compose UI, screens, previews, UI state/events, or files under `presentation/src/main`, load the `jetpack-compose-ui` skill first.

## Build And Verification
- Use the wrapper: `./gradlew ...`.
- Quick app build: `./gradlew :app:assembleDebug`.
- Full local build/check for a module: `./gradlew :module:build`.
- Android unit tests: `./gradlew :app:testDebugUnitTest` or `./gradlew :module:testDebugUnitTest` for Android library modules.
- JVM-only domain tests: `./gradlew :domain:test`.
- Focus a single JVM/unit test with Gradle's filter, for example `./gradlew :app:testDebugUnitTest --tests 'fr.cassette.cassette.ExampleUnitTest'`.
- Android lint exists only via AGP tasks; run `./gradlew :app:lint` or `./gradlew :module:lint` for Android modules.
- Instrumentation tests require a connected Android device/emulator: `./gradlew :app:connectedDebugAndroidTest`.

## Toolchain Notes
- Gradle daemon toolchain is pinned to Java 21 in `gradle/gradle-daemon-jvm.properties`; source/target compatibility is Java 11.
- Android modules use `compileSdk`/`targetSdk` 37 and `minSdk` 26.
- Dependencies and plugin versions are centralized in `gradle/libs.versions.toml`; add libraries there before using aliases in module Gradle files.
- There is no repo-local ktlint, detekt, formatter, CI, or pre-commit config at the time this file was written.
