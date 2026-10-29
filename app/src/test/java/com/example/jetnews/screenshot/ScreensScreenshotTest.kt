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

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import app.cash.paparazzi.DeviceConfig
import com.example.jetnews.data.posts.impl.post1
import com.example.jetnews.screenshot.ScreenshotFixtures.homeEmpty
import com.example.jetnews.screenshot.ScreenshotFixtures.homeInitialLoading
import com.example.jetnews.screenshot.ScreenshotFixtures.homeLoadFailed
import com.example.jetnews.screenshot.ScreenshotFixtures.homeRefreshFailed
import com.example.jetnews.screenshot.ScreenshotFixtures.homeRefreshing
import com.example.jetnews.screenshot.ScreenshotFixtures.homeWithPosts
import com.example.jetnews.ui.JetnewsAppFrame
import com.example.jetnews.ui.home.HomeFeedScreen
import com.example.jetnews.ui.home.HomeKey
import com.example.jetnews.ui.home.HomeListPaneMetadata
import com.example.jetnews.ui.home.HomeUiState
import com.example.jetnews.ui.navigation.ListDetailScene
import com.example.jetnews.ui.post.PostDetailPaneMetadata
import com.example.jetnews.ui.post.PostKey
import com.example.jetnews.ui.post.PostScreen
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
 * Full screens from the app module: the home feed (HomeFeedScreen) in each of its states, the
 * article screen (PostScreen) and the tablet layout. They are rendered at full device size, so
 * they also catch changes in spacing, dividers and section order between components.
 */
@Epic(VISUAL_REGRESSION_EPIC)
@Feature("Screens")
class ScreensScreenshotTest {

    @get:Rule(order = 0)
    val attachSnapshotsOnFailure = AttachSnapshotsOnFailure()

    @get:Rule(order = 1)
    val paparazzi = screenPaparazzi()

    @Test
    @DisplayName("TR-13 Home feed on a phone, light theme")
    @Story("Home feed")
    @Severity(SeverityLevel.BLOCKER)
    @Description("The first screen users see: top app bar, top story, recommended posts with one bookmarked, popular section.")
    fun homeFeed_phone_light() {
        paparazzi.snapshot {
            BrandTheme(darkTheme = false) { HomeFeed(uiState = homeWithPosts, showTopAppBar = true) }
        }
    }

    @Test
    @DisplayName("TR-14 Home feed on a phone, dark theme")
    @Story("Home feed")
    @Severity(SeverityLevel.CRITICAL)
    @Description("The same feed in the dark theme. Scaffold background, cards and dividers must use the dark colors.")
    fun homeFeed_phone_dark() {
        paparazzi.snapshot {
            BrandTheme(darkTheme = true) { HomeFeed(uiState = homeWithPosts, showTopAppBar = true) }
        }
    }

    @Test
    @DisplayName("TR-15 Home feed in wide-window mode on a tablet")
    @Story("Home feed")
    @Severity(SeverityLevel.NORMAL)
    @Description(
        "HomeFeedScreen as the app uses it in a wide window: the top app bar is replaced by an inline " +
            "search field and the popular section is hidden. Rendered alone at Pixel Tablet size; TR-32 " +
            "covers the full tablet layout with the navigation rail and the list-detail split.",
    )
    fun homeFeed_tablet_wideWindow() {
        paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL_TABLET)
        paparazzi.snapshot {
            BrandTheme(darkTheme = false) { HomeFeed(uiState = homeWithPosts, showTopAppBar = false) }
        }
    }

    @Test
    @DisplayName("TR-16 Home feed with no posts")
    @Story("Home feed")
    @Severity(SeverityLevel.NORMAL)
    @Description("No posts, not loading and no error: the screen shows the 'Tap to load content' button.")
    fun homeFeed_empty() {
        paparazzi.snapshot {
            BrandTheme(darkTheme = false) { HomeFeed(uiState = homeEmpty, showTopAppBar = true) }
        }
    }

    @Test
    @DisplayName("TR-17 Article screen on a phone, light theme")
    @Story("Article screen")
    @Severity(SeverityLevel.CRITICAL)
    @Description("PostScreen with the top app bar, header image, title, metadata, body text and the bottom action bar.")
    fun articleScreen_phone_light() {
        paparazzi.snapshot {
            BrandTheme(darkTheme = false) { Article() }
        }
    }

    @Test
    @DisplayName("TR-18 Article screen on a phone, dark theme")
    @Story("Article screen")
    @Severity(SeverityLevel.NORMAL)
    @Description("The same article in the dark theme, including the code block background.")
    fun articleScreen_phone_dark() {
        paparazzi.snapshot {
            BrandTheme(darkTheme = true) { Article() }
        }
    }

    @Test
    @DisplayName("TR-28 Home feed, first load in progress")
    @Story("Home feed states")
    @Severity(SeverityLevel.CRITICAL)
    @Description("No posts yet and loading: the screen shows a full-screen progress indicator under the top app bar.")
    fun homeFeed_initialLoading() {
        paparazzi.snapshotAfter(ENTER_ANIMATIONS_DONE_MILLIS) {
            BrandTheme(darkTheme = false) { HomeFeed(uiState = homeInitialLoading, showTopAppBar = true) }
        }
    }

    @Test
    @DisplayName("TR-29 Home feed, pull to refresh in progress")
    @Story("Home feed states")
    @Severity(SeverityLevel.NORMAL)
    @Description(
        "Posts are shown and a refresh is running: the pull-to-refresh indicator sits at the top of the list and " +
            "the posts stay visible. The pull gesture itself is interaction and is not part of a screenshot test.",
    )
    fun homeFeed_refreshing() {
        paparazzi.snapshotAfter(ENTER_ANIMATIONS_DONE_MILLIS) {
            BrandTheme(darkTheme = false) { HomeFeed(uiState = homeRefreshing, showTopAppBar = true) }
        }
    }

    @Test
    @DisplayName("TR-30 Home feed, refresh failed")
    @Story("Home feed states")
    @Severity(SeverityLevel.CRITICAL)
    @Description(
        "A refresh failed while posts were shown: the posts stay and the snackbar says " +
            "'Can't update latest news' with a Retry action.",
    )
    fun homeFeed_refreshFailed() {
        paparazzi.snapshotAfter(ENTER_ANIMATIONS_DONE_MILLIS) {
            BrandTheme(darkTheme = false) { HomeFeed(uiState = homeRefreshFailed, showTopAppBar = true) }
        }
    }

    @Test
    @DisplayName("TR-31 Home feed, first load failed")
    @Story("Home feed states")
    @Severity(SeverityLevel.CRITICAL)
    @Description("The first load failed: no posts, an empty screen and the snackbar with a Retry action.")
    fun homeFeed_loadFailed() {
        paparazzi.snapshotAfter(ENTER_ANIMATIONS_DONE_MILLIS) {
            BrandTheme(darkTheme = false) { HomeFeed(uiState = homeLoadFailed, showTopAppBar = true) }
        }
    }

    @Test
    @DisplayName("TR-32 Tablet layout: navigation rail, feed and article side by side")
    @Story("Adaptive layout")
    @Severity(SeverityLevel.CRITICAL)
    @Description(
        "The layout the app shows on a tablet when a post is open: the navigation rail (JetnewsAppFrame), the 334 dp " +
            "feed pane and the article pane (ListDetailScene with the app's own pane metadata). Navigation and view " +
            "models are not involved; the panes get fixed UI state.",
    )
    fun tablet_listDetailWithNavRail() {
        paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL_TABLET)
        paparazzi.snapshot {
            BrandTheme(darkTheme = false) {
                // JetnewsNavDisplay hosts every scene in a Surface; the frame and rail sit outside it.
                JetnewsAppFrame(isExpandedScreen = true, currentTopLevelKey = HomeKey, navigate = {}) {
                    Surface { TabletListDetail() }
                }
            }
        }
    }

    /**
     * The scene the app's ListDetailSceneStrategy builds for the back stack [HomeKey, PostKey]:
     * the home entry as the list pane and the post entry as the detail pane, each with the
     * metadata the app's entries use.
     */
    @Composable
    private fun TabletListDetail() {
        val scene = remember {
            ListDetailScene<NavKey>(
                key = HomeKey,
                listEntry = NavEntry<NavKey>(HomeKey, metadata = HomeListPaneMetadata) {
                    HomeFeed(uiState = homeWithPosts, showTopAppBar = false)
                },
                detailEntry = NavEntry<NavKey>(PostKey(post1.id), metadata = PostDetailPaneMetadata) {
                    Article(isExpandedScreen = true)
                },
                previousEntries = emptyList(),
            )
        }
        scene.content()
    }

    @Composable
    private fun HomeFeed(uiState: HomeUiState, showTopAppBar: Boolean) {
        HomeFeedScreen(
            uiState = uiState,
            showTopAppBar = showTopAppBar,
            onToggleFavorite = {},
            onSelectPost = {},
            onRefreshPosts = {},
            onErrorDismiss = {},
            openDrawer = {},
            homeListLazyListState = rememberLazyListState(),
            snackbarHostState = remember { SnackbarHostState() },
            onSearchInputChanged = {},
        )
    }

    @Composable
    private fun Article(isExpandedScreen: Boolean = false) {
        PostScreen(
            post = post1,
            isExpandedScreen = isExpandedScreen,
            onBack = {},
            isFavorite = false,
            onToggleFavorite = {},
        )
    }
}
