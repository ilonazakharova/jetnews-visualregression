# Screenshot testing strategy for BILD and WELT apps

**Goal:** every pull request on iOS and Android renders real UI from the production code, feeds it
fixed data, and fails when a screen looks different from its approved reference image (golden).
The Android part is implemented in this repository; the iOS part is a plan.

## 1. Tools and why

| | Android | iOS |
| :-- | :-- | :-- |
| Tool | [Paparazzi](https://github.com/cashapp/paparazzi) | [swift-snapshot-testing](https://github.com/pointfreeco/swift-snapshot-testing) (Point-Free) |
| Renders | Compose UI on the JVM via layoutlib, no emulator | SwiftUI and UIKit views inside the test process on a simulator |
| CI runner | Linux | macOS, pinned Xcode and simulator version |
| Speed in this repo | 24 snapshots verified in about 1 to 3 minutes per PR, including the build (measured before the suite grew to 32) | not measured |

Both tools test **components and screens in isolation**, at the level where visual bugs are made:
a card, a banner, a feed. That is fast enough to run on every PR and needs no backend.

Alternatives and when they make sense:

- **Roborazzi** (Android, Robolectric) is a valid alternative to Paparazzi when tests also need
  real Android behavior such as clicks before the snapshot. Paparazzi was chosen because it is
  simpler and faster for pure rendering.
- **Maestro, Appium or XCUITest with a device cloud** (Sauce Labs, BrowserStack) plus a visual
  service (Applitools, Emerge) test the whole running app. They are slower, need a mock server and
  are more flaky, so they belong in a nightly or pre-release run for a few end-to-end journeys,
  not in every PR.

One tool per platform, but one **shared process**: the same scenario list, fixture data, naming,
review rules and CI gate (sections 2, 3 and 4). This is what keeps two platforms from doubling the
effort.

## 2. Deterministic data

A snapshot is only useful if the same code always renders the same pixels.

**Where the data comes in.** UI code reads data through an interface, not from the network
directly. In this repository the home feed gets a `PostsFeed` from the `PostsRepository`
interface; the app's fake implementation returns fixed posts, a fixed breaking-news item and a
premium flag. Tests go one step further and build the UI state (`HomeUiState`) directly from
these fixtures, so no repository, view model or coroutine is involved.

**What is fixed:**
- data: fixed texts, fixed dates such as "Updated 10:45", local images (drawables or asset
  catalogs), never a URL;
- environment: device size, density, font scale, locale and layout direction are set per test;
- theme: brand color schemes. Dynamic color (Android 12+) depends on the user's wallpaper and is
  not snapshot-tested;
- time and animation: no clock reads in UI code under test; Paparazzi renders a single frame.

**Edge cases are copies of real fixtures with one field changed**, for example a long German
compound headline, Arabic text or `isPremium = true`. Every other field keeps the shape the app
really uses.

**Shared fixtures for both platforms (plan).** Keep the scenario fixtures as JSON in one shared
location (for example a `snapshot-fixtures` module or repository) with stable scenario IDs:
`breaking_news.long_german_headline`, `teaser.premium`, `feed.offline`. Android and iOS tests load
the same JSON into their own models. A product owner or designer then reviews one list of
scenarios, and both platforms cover the same states.

## 3. Goldens: storage and review

- **Storage.** Goldens are PNG files committed next to the tests
  (`app/src/test/snapshots/images/`), so a UI change and its new images arrive in the same pull
  request. When the image folder grows past a few hundred megabytes, move it to Git LFS.
- **Recording.** Goldens are recorded only on CI, on the same runner type that verifies them, so
  fonts and anti-aliasing match. A manual workflow run records them and pushes them to a separate
  `goldens/review-<run id>` branch. Nothing is approved automatically.
- **Review.** A person opens every new or changed image and checks it against a checklist: cropped
  to the component, dark mode really dark, text complete or cut with an ellipsis where the design
  says so, RTL mirrored, real images, no blank images. Approved images are then brought into the
  feature branch.
- **Ownership (plan).** A `CODEOWNERS` entry for the snapshots folder makes a designer or the
  owning team a required reviewer for intentional visual changes.

The first two reviews in this project found five real problems before approval: a test that
stretched a card to full height, two product issues (no hyphenation in German, an untranslated
string in Arabic), Paparazzi's own color palette shown instead of the brand colors, and a
mid-word line break in the new banner at 200 % font size. That is the point of the review step:
a golden is only as good as the review it got.

A golden may record a known product defect only when the defect has a ticket and an owner. The
two product issues above are filed as KI-1 and KI-2 in [docs/known-issues.md](docs/known-issues.md),
and the test descriptions name them. Otherwise the baseline quietly turns a bug into the expected
design, and the fix later looks like a regression.

## 4. CI/CD

Implemented in `.github/workflows/scheduled_tests.yml`:

| Job | When | What |
| :-- | :-- | :-- |
| lint | every PR and push to main | `spotlessCheck` (ktlint and license headers) |
| verify | every PR, push to main, nightly 02:00 UTC | `verifyPaparazziDebug`; Allure report; on failure, delta images (golden, diff, actual) as an artifact and the list of failed snapshots in the job summary |
| record | manual | records goldens and pushes them to a review branch |

Next step for production: make verify a required check in branch protection, so a PR cannot be
merged with an unexplained visual change. (In this demo repository it reports on every PR but is
not enforced.)
An optional Telegram alert fires only when a nightly or main run fails. iOS gets the same three
jobs on a macOS runner.

## 5. Stability (flakiness)

- Rendering is off-device and single-frame, so system bars, animations and network timing cannot
  change the image.
- Data, clock, fonts, theme and device configuration are fixed (section 2).
- Runner images, Xcode and simulator versions are pinned; changing them is its own pull request
  that re-records all goldens.
- A small pixel tolerance (Paparazzi's default is 0.01 %; on iOS a `precision` of about 0.98)
  absorbs anti-aliasing noise without hiding real changes.
- No automatic retries. A snapshot that fails once and passes on retry is a bug in the test setup
  and gets fixed or quarantined with an owner and a date.

How to check it: the manual **determinism** run of the CI workflow (`check_determinism`) records
all goldens on two fresh runners and fails unless both runs produce byte-identical images. Run it
after every toolchain or runner-image change. Link the latest run here when you quote the result:
_no run linked yet_.

## 6. Scaling

- **Per feature module.** Each feature (feed, article, paywall, live ticker) keeps its snapshot
  tests and goldens in its own module, owned by its team.
- **Only what changed.** On a PR, run snapshot tests only for modules affected by the change; run
  everything nightly.
- **Shards.** Split the suite across parallel CI jobs when it grows past a few minutes.
- **One scenario matrix.** Light and dark, font scale 1.0, 1.5 and 2.0, phone and tablet, German
  and one RTL locale. Not every component needs every combination; the matrix is agreed per
  component in refinement.

## 7. Bringing it into a large existing codebase

1. **Find the seam.** Most production apps already have a repository or use-case layer. Tests
   inject fixtures there, or better, build the screen's UI state directly. Where UI code calls the
   network itself, the first step is to extract an interface for that call. For full-screen tests
   that must run the real data layer, a local HTTP stub (OkHttp `MockWebServer` on Android,
   `URLProtocol` stubs on iOS) serves the same JSON fixtures.
2. **Fixtures live with the tests** of each feature module, and shared scenario JSON in one shared
   module (section 2).
3. **Start without blocking anyone.** Add the suite as a non-required check for two sprints while
   goldens are reviewed and flaky tests removed. Then make it required.
4. **Start where bugs hurt most:** breaking-news banner, teaser cards and the paywall badge, because
   a broken paywall badge costs revenue. Cover the badge in **every** teaser type that can show a
   paywalled article, not only the most common one; in this repository each of the four feed card
   types has its own premium test.

## 8. Team and Scrum

- **Definition of Done:** a UI change includes updated, reviewed goldens; the PR shows the delta
  images.
- **Refinement:** the team agrees on the state matrix for new components (section 6), so tests are
  written with the feature, not after it.
- **Developers** write and update snapshot tests; **QA** owns the framework, the review checklist
  and the scenario list, and reviews goldens; **designers** approve intentional visual changes;
  the **PO** sees which states are covered in one list.
- **Mentoring:** pair on the first tests in each team, review goldens together until the checklist
  is second nature.

## 9. Rollout

| Phase | Duration | Outcome |
| :-- | :-- | :-- |
| Pilot | 2 sprints | One feature (feed teasers) on Android and iOS, shared fixtures, CI jobs, review checklist. Check runtime and flakiness. |
| Gate | 1 sprint | Verify becomes a required check; goldens part of the Definition of Done. |
| Expand | following sprints | Feature teams add tests for their components in priority order; QA pairs with each team on its first tests. |
| Mature | ongoing | Affected-module runs, sharding, quarterly review of the scenario matrix and flaky-test quarantine. |

**What this demo does not cover yet:** the iOS implementation, live tickers, video teasers, ads,
and offline or error image states. They would follow the same pattern: a component in the app, a
fixture for each state, one test per state. The feed's own states (first load, refresh, refresh
failed, load failed) and the tablet list-detail layout are covered.
