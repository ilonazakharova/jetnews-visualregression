# Known product issues

Two approved goldens record behavior that is wrong for the product. A golden that locks in a
defect is only acceptable if the defect has a ticket and an owner, so each one is described here
in ticket form and filed as a GitHub issue; the **Ticket** row links to it.

When a fix lands, the matching test fails on purpose. Record new goldens on CI, review them, and
remove the "Known product issue" note from the test description and from this page in the same
pull request.

## KI-1 Long German words break mid-word without a hyphen

| | |
| :-- | :-- |
| Golden | TR-03 `FeedCardsScreenshotTest.topStoryCard_longGermanHeadline` |
| Component | `PostCardTop` (`ui/home/PostCardTop.kt`); the same applies to every title in the app |
| Severity | Major for a German publisher: headlines are the product |
| Owner | Feed team (Android); copy editors to confirm the expected style |
| Ticket | [#2](https://github.com/ilonazakharova/jetnews-visualregression/issues/2) |

**Steps.** Show a post whose title contains a long compound word, for example
"Rechtsdurchsetzungsverbesserungsgesetz", in a card narrower than the word.

**Actual.** The word breaks at an arbitrary letter with no hyphen ("Rechtsdurchsetzungsverbe" /
"sserungsgesetz").

**Expected.** The word breaks at a syllable boundary and the first part ends with a hyphen.

**Likely fix.** Compose does not hyphenate by default. Set `hyphens = Hyphens.Auto` and
`lineBreak = LineBreak.Paragraph` in the title text styles (`ui/theme/Type.kt`), and make sure the
text locale is German, because hyphenation patterns come from the locale.

## KI-2 "min read" is not translated in Arabic

| | |
| :-- | :-- |
| Golden | TR-12 `AccessibilityAndLocaleScreenshotTest.simpleCard_rtlArabic` |
| Component | `AuthorAndReadTime` in `PostCardSimple` (`ui/home/PostCards.kt`), string `home_post_min_read` |
| Severity | Minor: the line is readable but looks broken to Arabic readers |
| Owner | Localization; feed team for the string resource |
| Ticket | [#3](https://github.com/ilonazakharova/jetnews-visualregression/issues/3) |

**Steps.** Set the device language to Arabic and open the home feed.

**Actual.** The metadata line mixes Arabic and English. Bidirectional ordering shows it as
"min read 1 - <author>".

**Expected.** The whole line is in Arabic and reads right to left.

**Likely fix.** Add `values-ar/strings.xml` with Arabic translations of `home_post_min_read` and
`post_min_read`. Check the other user-facing strings at the same time, because the app has no
Arabic resources at all.
