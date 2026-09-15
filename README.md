This is a Kotlin Multiplatform project targeting Android, iOS.

* [/androidApp](./androidApp/src/main) contains the native Android application built with Jetpack Compose.

* [/iosApp](./iosApp/iosApp) contains the native iOS application built with SwiftUI.

* [/sharedLogic](./sharedLogic/src) contains the Kotlin code shared between Android and iOS:
  - [commonMain](./sharedLogic/src/commonMain/kotlin): domain models, repositories, API service, DTOs, Ktor configuration and Koin setup.
  - [androidMain](./sharedLogic/src/androidMain/kotlin): Android-specific implementations, including the OkHttp engine.
  - [iosMain](./sharedLogic/src/iosMain/kotlin): iOS-specific implementations, including the Darwin engine and Swift-facing news client.

UI and presentation state are implemented separately in each native application.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…