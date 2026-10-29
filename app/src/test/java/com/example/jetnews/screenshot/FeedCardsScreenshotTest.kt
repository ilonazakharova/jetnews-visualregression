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

import com.example.jetnews.data.posts.impl.post1
import com.example.jetnews.data.posts.impl.post4
import com.example.jetnews.data.posts.impl.post5
import com.example.jetnews.screenshot.ScreenshotFixtures.feed
import com.example.jetnews.screenshot.ScreenshotFixtures.longGermanHeadlinePost
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
 * The cards of the home feed, taken from the app module (ui/home/PostCards.kt,
 * PostCardTop.kt and PostCardYourNetwork.kt). A change to any of them fails these tests.
 */
@Epic(VISUAL_REGRESSION_EPIC)
@Feature("Home feed cards")
class FeedCardsScreenshotTest {

    @get:Rule(order = 0)
    val attachSnapshotsOnFailure = AttachSnapshotsOnFailure()

    @get:Rule(order = 1)
    val paparazzi = componentPaparazzi()

    @Test
    @DisplayName("TR-01 Top story card, light theme")
    @Story("Top story card")
    @Severity(SeverityLevel.CRITICAL)
    @Description("PostCardTop with the highlighted post: hero image, title, author and read time.")
    fun topStoryCard_light() {
        paparazzi.snapshot {
            ThemedSurface { PostCardTop(post = feed.highlightedPost) }
        }
    }

    @Test
    @DisplayName("TR-02 Top story card, dark theme")
    @Story("Top story card")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Same card in the dark theme. Background, title and metadata colors must all change.")
    fun topStoryCard_dark() {
        paparazzi.snapshot {
            ThemedSurface(darkTheme = true) { PostCardTop(post = feed.highlightedPost) }
        }
    }

    @Test
    @DisplayName("TR-03 Top story card, long German compound headline")
    @Story("Top story card")
    @Severity(SeverityLevel.NORMAL)
    @Description(
        "PostCardTop has no line limit. The whole headline must wrap inside the card, without clipping. " +
            "Known product issue: the app does not enable hyphenation, so long German words break " +
            "mid-word without a hyphen. The golden records this current behavior; ticket KI-1 in docs/known-issues.md.",
    )
    fun topStoryCard_longGermanHeadline() {
        paparazzi.snapshot {
            ThemedSurface { PostCardTop(post = longGermanHeadlinePost) }
        }
    }

    @Test
    @DisplayName("TR-04 Simple card with thumbnail")
    @Story("Simple card")
    @Severity(SeverityLevel.CRITICAL)
    @Description("PostCardSimple: thumbnail image, title, author and read time, bookmark button off.")
    fun simpleCard_default() {
        paparazzi.snapshot {
            ThemedSurface { PostCardSimple(post = post1, navigateToPost = {}, isFavorite = false, onToggleFavorite = {}) }
        }
    }

    @Test
    @DisplayName("TR-05 Simple card, bookmarked")
    @Story("Simple card")
    @Severity(SeverityLevel.NORMAL)
    @Description("PostCardSimple with the bookmark button in its checked state.")
    fun simpleCard_bookmarked() {
        paparazzi.snapshot {
            ThemedSurface { PostCardSimple(post = post1, navigateToPost = {}, isFavorite = true, onToggleFavorite = {}) }
        }
    }

    @Test
    @DisplayName("TR-06 Simple card, long German headline is cut at 3 lines")
    @Story("Simple card")
    @Severity(SeverityLevel.NORMAL)
    @Description(
        "The app's PostTitle limits titles to 3 lines with an ellipsis. The limit comes from the " +
            "product code, not from the test.",
    )
    fun simpleCard_longGermanHeadline_truncated() {
        paparazzi.snapshot {
            ThemedSurface {
                PostCardSimple(post = longGermanHeadlinePost, navigateToPost = {}, isFavorite = false, onToggleFavorite = {})
            }
        }
    }

    @Test
    @DisplayName("TR-07 History card")
    @Story("History card")
    @Severity(SeverityLevel.NORMAL)
    @Description("PostCardHistory: 'based on your history' label, title, metadata and the overflow menu icon.")
    fun historyCard_default() {
        paparazzi.snapshot {
            ThemedSurface { PostCardHistory(post = post4, navigateToPost = {}) }
        }
    }

    @Test
    @DisplayName("TR-08 Popular card, long German headline is cut at 2 lines")
    @Story("Popular card")
    @Severity(SeverityLevel.NORMAL)
    @Description("PostCardPopular is 280 dp wide and limits the title to 2 lines. The image and author stay visible.")
    fun popularCard_longGermanHeadline_truncated() {
        paparazzi.snapshot {
            ThemedSurface { PopularCardInRow(post = longGermanHeadlinePost) }
        }
    }

    @Test
    @DisplayName("TR-09 Popular card, dark theme")
    @Story("Popular card")
    @Severity(SeverityLevel.NORMAL)
    @Description("PostCardPopular in the dark theme: card container and text colors come from the dark color scheme.")
    fun popularCard_dark() {
        paparazzi.snapshot {
            ThemedSurface(darkTheme = true) { PopularCardInRow(post = post5) }
        }
    }
}
