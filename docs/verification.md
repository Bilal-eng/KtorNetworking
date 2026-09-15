# News app verification

## Automated checks

Commands run for this verification:

```sh
./gradlew :sharedLogic:testAndroidHostTest :androidApp:testDebugUnitTest :sharedLogic:iosSimulatorArm64Test :androidApp:assembleDebug
# Re-run after adding the two detail bridge regression tests:
./gradlew :sharedLogic:iosSimulatorArm64Test
```

| Suite | Tests | Failures |
| --- | ---: | ---: |
| Shared Android host tests (including common DTO tests) | 11 | 0 |
| Android list/detail ViewModel tests | 7 | 0 |
| iOS simulator tests (including common DTO tests) | 9 | 0 |

The 27 executions include the same 3 common DTO tests on both platforms.

Existing checks cover JSON mapping, repository errors, selected detail ID, Android loading/success/error transitions, retry, duplicate request prevention and cancellation, plus iOS bridge success/error/cancellation.

Added iOS bridge regressions:

- A failed detail request can be retried with the same ID and return success.
- Cancelling a pending detail request suppresses its callbacks and allows a different article to load.

These use a fake repository and a coroutine test scheduler. They do not simulate a physical network outage or exercise SwiftUI.

## Live iOS check

On an iPhone 17 Pro simulator, using the built debug app and the live API:

- News list loaded with titles and images.
- Selected "Celebrate International Observe the Moon Night with NASA"; detail displayed the matching title, source, author, date and summary.
- Returned to the list; loaded cards remained visible.
- Selected "Artemis III Crew Visits NASA Kennedy’s Vehicle Assembly Building"; detail changed to that article.
- During the second image load, article text was visible independently of the image.

## Live Android follow-up

Verified on Pixel 4 (Android 15 / API 35) using ADB with explicit user authorization after the desktop UI surface could not access the emulator:

- Live list displayed article titles and cards.
- Opened "Launch Preview: Nine orbital launches scheduled from sites around the world"; detail showed the matching title, image, source, author Eleanor Day, publication date and summary.
- Returned to the list; the same cards and positions remained visible.
- Disabled Wi-Fi and mobile data on the emulator, then opened "Celebrate International Observe the Moon Night with NASA". The detail error message and retry button appeared.
- Restored both network settings and tapped retry. The selected NASA article loaded with the correct title, source, author, date and summary.
- Returned again; both original list titles were present.
- Confirmed Wi-Fi and mobile data were restored to their original enabled values.

No production fix was needed for these scenarios.

## Remaining manual checks

The following are still manual checks, not claims of passing automated coverage:

1. On iOS, load the list, disconnect the test device's network, open a detail, wait for the error, restore connectivity and retry. Confirm the same article loads.
2. With a slow connection, open a detail and return before completion; open another article and confirm no stale content or error appears.
3. With a failing image URL, confirm the fallback icon appears and the article text remains visible.

The Swift models' MainActor-queued callback ID guards are reviewed in code but are not independently covered by Swift unit tests. Bridge cancellation tests must not be treated as proof of that Swift scheduling behavior.

No production implementation was changed in this verification pass.
