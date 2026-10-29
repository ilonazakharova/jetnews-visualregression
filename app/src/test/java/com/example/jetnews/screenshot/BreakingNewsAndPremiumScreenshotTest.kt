/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetnews.screenshot

import app.cash.paparazzi.DeviceConfig
import com.example.jetnews.screenshot.ScreenshotFixtures.breakingNews
import com.example.jetnews.screenshot.ScreenshotFixtures.longGermanBreakingNews
import com.example.jetnews.screenshot.ScreenshotFixtures.premiumHistoryPost
import com.example.jetnews.screenshot.ScreenshotFixtures.premiumPopularPost
import com.example.jetnews.screenshot.ScreenshotFixtures.premiumPost
import com.example.jetnews.screenshot.ScreenshotFixtures.premiumTopPost
import com.example.jetnews.ui.home.BreakingNewsBanner
import com.example.jetnews.ui.home.PostCardHistory
import com.example.jetnews.ui.home.PostCardSimple
import com.example.jetnews.ui.home.PostCardTop
import io.qameta.allure.Description
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Severity
import io.qameta.allure.SeverityLevel
import io.qameta.allure.Story
import io.qameta.allure.junit4.DisplayName
import org.junit.Rule
import org.junit.Test

/**
 * The news-specific components added to the app: the breaking-news banner at the top of the
 * feed (ui/home/BreakingNewsBanner.kt) and the premium badge on paywalled articles
 * (ui/home/PremiumBadge.kt). Every card type of the feed shows the badge, so every card type
 * has a premium test.
 */
@Epic(VISUAL_REGRESSION_EPIC)
@Feature("Breaking news and premium content")
class BreakingNewsAndPremiumScreenshotTest {

    @get:Rule(order = 0)
    val attachSnapshotsOnFailure = AttachSnapshotsOnFailure()

    @get:Rule(order = 1)
    val paparazzi = componentPaparazzi()

    @Test
    @DisplayName("TR-19 Breaking-news banner, light theme")
    @Story("Breaking-news banner")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Label, update time, headline and summary on the error-container color from the theme.")
    fun breakingNewsBanner_light() {
        paparazzi.snapshot {
            ThemedSurface { BreakingNewsBanner(breakingNews = breakingNews, onClick = {}) }
        }
    }

    @Test
    @DisplayName("TR-20 Breaking-news banner, dark theme")
    @Story("Breaking-news banner")
    @Severity(SeverityLevel.BLOCKER)
    @Description("The banner in the dark theme. Its colors come from the theme, so they must differ from TR-19.")
    fun breakingNewsBanner_dark() {
        paparazzi.snapshot {
            ThemedSurface(darkTheme = true) { BreakingNewsBanner(breakingNews = breakingNews, onClick = {}) }
        }
    }

    @Test
    @DisplayName("TR-21 Breaking-news banner, long German headline")
    @Story("Breaking-news banner")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Long German compound words: the headline stops at 3 lines and the summary at 2, both with an ellipsis.")
    fun breakingNewsBanner_longGermanHeadline() {
        paparazzi.snapshot {
            ThemedSurface { BreakingNewsBanner(breakingNews = longGermanBreakingNews, onClick = {}) }
        }
    }

    @Test
    @DisplayName("TR-22 Breaking-news banner at 200% font size")
    @Story("Breaking-news banner")
    @Severity(SeverityLevel.CRITICAL)
    @Description("System font scale 2.0. The label and update time may wrap; nothing may be clipped.")
    fun breakingNewsBanner_fontScale200() {
        paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL_5.copy(fontScale = 2.0f))
        paparazzi.snapshot {
            ThemedSurface { BreakingNewsBanner(breakingNews = breakingNews, onClick = {}) }
        }
    }

    @Test
    @DisplayName("TR-23 Premium article card, light theme")
    @Story("Premium badge")
    @Severity(SeverityLevel.BLOCKER)
    @Description("A paywalled article in PostCardSimple shows the Premium badge above the title. A missing badge costs revenue.")
    fun premiumCard_light() {
        paparazzi.snapshot {
            ThemedSurface { PostCardSimple(post = premiumPost, navigateToPost = {}, isFavorite = false, onToggleFavorite = {}) }
        }
    }

    @Test
    @DisplayName("TR-24 Premium article card, dark theme")
    @Story("Premium badge")
    @Severity(SeverityLevel.CRITICAL)
    @Description("The Premium badge in the dark theme must stay readable against the dark background.")
    fun premiumCard_dark() {
        paparazzi.snapshot {
            ThemedSurface(darkTheme = true) {
                PostCardSimple(post = premiumPost, navigateToPost = {}, isFavorite = false, onToggleFavorite = {})
            }
        }
    }

    @Test
    @DisplayName("TR-25 Premium top story card")
    @Story("Premium badge")
    @Severity(SeverityLevel.BLOCKER)
    @Description("A paywalled article as the top story: PostCardTop shows the Premium badge between the hero image and the title.")
    fun premiumTopStoryCard_light() {
        paparazzi.snapshot {
            ThemedSurface { PostCardTop(post = premiumTopPost) }
        }
    }

    @Test
    @DisplayName("TR-26 Premium popular card")
    @Story("Premium badge")
    @Severity(SeverityLevel.CRITICAL)
    @Description("A paywalled article in the popular section: PostCardPopular shows the Premium badge above the title.")
    fun premiumPopularCard_light() {
        paparazzi.snapshot {
            ThemedSurface { PopularCardInRow(post = premiumPopularPost) }
        }
    }

    @Test
    @DisplayName("TR-27 Premium history card")
    @Story("Premium badge")
    @Severity(SeverityLevel.CRITICAL)
    @Description("A paywalled article in the history section: PostCardHistory shows the Premium badge above the title.")
    fun premiumHistoryCard_light() {
        paparazzi.snapshot {
            ThemedSurface { PostCardHistory(post = premiumHistoryPost, navigateToPost = {}) }
        }
    }
}
