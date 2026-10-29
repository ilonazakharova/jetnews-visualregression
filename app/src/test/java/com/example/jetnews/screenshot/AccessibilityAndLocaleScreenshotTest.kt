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
import com.android.resources.LayoutDirection
import com.example.jetnews.data.posts.impl.post1
import com.example.jetnews.screenshot.ScreenshotFixtures.arabicPost
import com.example.jetnews.ui.home.PostCardSimple
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
 * The simple feed card under system settings that change layout: font size and a
 * right-to-left locale. Each test sets the device configuration, so the app reacts the same
 * way it does on a phone with these settings.
 */
@Epic(VISUAL_REGRESSION_EPIC)
@Feature("Accessibility and localization")
class AccessibilityAndLocaleScreenshotTest {

    @get:Rule(order = 0)
    val attachSnapshotsOnFailure = AttachSnapshotsOnFailure()

    // supportsRtl matches android:supportsRtl="true" in the app manifest. Without it
    // Paparazzi ignores the RTL layout direction.
    @get:Rule(order = 1)
    val paparazzi = componentPaparazzi(supportsRtl = true)

    @Test
    @DisplayName("TR-10 Simple card at 150% font size")
    @Story("Large font")
    @Severity(SeverityLevel.CRITICAL)
    @Description("System font scale 1.5. Title, metadata and bookmark button must not overlap or clip.")
    fun simpleCard_fontScale150() {
        paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL_5.copy(fontScale = 1.5f))
        paparazzi.snapshot {
            ThemedSurface { PostCardSimple(post = post1, navigateToPost = {}, isFavorite = false, onToggleFavorite = {}) }
        }
    }

    @Test
    @DisplayName("TR-11 Simple card at 200% font size")
    @Story("Large font")
    @Severity(SeverityLevel.NORMAL)
    @Description("System font scale 2.0, the largest Android offers. The card grows in height; the title keeps its 3-line limit.")
    fun simpleCard_fontScale200() {
        paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL_5.copy(fontScale = 2.0f))
        paparazzi.snapshot {
            ThemedSurface { PostCardSimple(post = post1, navigateToPost = {}, isFavorite = false, onToggleFavorite = {}) }
        }
    }

    @Test
    @DisplayName("TR-12 Simple card in Arabic, right-to-left")
    @Story("Right-to-left layout")
    @Severity(SeverityLevel.NORMAL)
    @Description(
        "Arabic locale with RTL layout direction and Arabic content. The thumbnail moves to the right, " +
            "the bookmark button to the left, and the text is right-aligned. Known product issue: " +
            "'min read' has no Arabic translation, so the metadata line mixes Arabic and English and " +
            "reads as 'min read 1 - <author>'. The golden records this current behavior; ticket KI-2 in docs/known-issues.md.",
    )
    fun simpleCard_rtlArabic() {
        paparazzi.unsafeUpdateConfig(
            deviceConfig = DeviceConfig.PIXEL_5.copy(layoutDirection = LayoutDirection.RTL, locale = "ar"),
        )
        paparazzi.snapshot {
            ThemedSurface { PostCardSimple(post = arabicPost, navigateToPost = {}, isFavorite = false, onToggleFavorite = {}) }
        }
    }
}
