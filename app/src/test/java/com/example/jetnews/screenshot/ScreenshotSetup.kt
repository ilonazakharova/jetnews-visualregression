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

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import com.example.jetnews.model.Post
import com.example.jetnews.ui.home.PostCardPopular
import com.example.jetnews.ui.theme.JetnewsTheme

/**
 * The window theme. It is the parent of the app's own `Theme.Jetnews` (res/values/themes.xml),
 * which adds nothing to it, so the window looks the same as in the app.
 */
private const val WINDOW_THEME = "android:Theme.Material.NoActionBar"

/** Epic shared by every screenshot test, so the Allure report groups them together. */
const val VISUAL_REGRESSION_EPIC = "Visual regression"

/**
 * Paparazzi rule for one component.
 *
 * [RenderingMode.SHRINK] crops the image to the component's measured size. Without it the
 * golden is the full device height and a component that grows or shrinks still "fits".
 */
fun componentPaparazzi(deviceConfig: DeviceConfig = DeviceConfig.PIXEL_5, supportsRtl: Boolean = false): Paparazzi = Paparazzi(
    deviceConfig = deviceConfig,
    theme = WINDOW_THEME,
    renderingMode = RenderingMode.SHRINK,
    supportsRtl = supportsRtl,
)

/** Paparazzi rule for a full screen, rendered at the device's full size. */
fun screenPaparazzi(deviceConfig: DeviceConfig = DeviceConfig.PIXEL_5): Paparazzi = Paparazzi(
    deviceConfig = deviceConfig,
    theme = WINDOW_THEME,
    renderingMode = RenderingMode.NORMAL,
)

/**
 * [JetnewsTheme] with the app's brand colors.
 *
 * On a phone, Android 12 and later replace them with dynamic colors from the user's wallpaper,
 * which differ for every user, so they cannot be a golden. Paparazzi also substitutes its own
 * default wallpaper palette, which shows colors the app never designed. The tests therefore turn
 * dynamic color off and check the brand light and dark schemes.
 */
@Composable
fun BrandTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    JetnewsTheme(darkTheme = darkTheme, dynamicColor = false, content = content)
}

/**
 * Hosts a component the way the app does: inside the theme and a [Surface]. The Surface paints
 * the theme background and sets the content color, so dark mode is really dark.
 */
@Composable
fun ThemedSurface(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    BrandTheme(darkTheme = darkTheme) {
        Surface(content = content)
    }
}

/**
 * PostCardPopular pushes the author to the bottom with a weighted Spacer, so on its own it
 * fills all the height it is given. The app places it in a Row with
 * `height(IntrinsicSize.Max)` (PostListPopularSection in HomeScreens.kt), which sizes the
 * card to its content. The tests do the same.
 */
@Composable
fun PopularCardInRow(post: Post) {
    Row(modifier = Modifier.height(IntrinsicSize.Max)) {
        PostCardPopular(post = post, navigateToPost = {})
    }
}

/**
 * Takes the snapshot [offsetMillis] after the first frame instead of at the first frame.
 *
 * Some states only appear through an animation that starts when the screen is shown: the
 * pull-to-refresh indicator slides in, and a snackbar fades and scales in. At the first frame
 * they are still invisible. Paparazzi's clock is fixed, so a snapshot at the same offset is
 * always the same frame.
 */
fun Paparazzi.snapshotAfter(offsetMillis: Long, content: @Composable () -> Unit) {
    val view = ComposeView(context).apply { setContent(content) }
    snapshot(view = view, offsetMillis = offsetMillis)
}

/** Long enough for the refresh indicator and the snackbar to finish their enter animations. */
const val ENTER_ANIMATIONS_DONE_MILLIS = 1_000L
