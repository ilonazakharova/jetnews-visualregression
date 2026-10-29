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

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.jetnews.R
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
 * Messages the home feed shows over its content.
 *
 * The feed shows its error in a snackbar that fades and scales in. Paparazzi does not capture
 * that enter animation on the full screen (TR-31 has no snackbar), so the snackbar is checked
 * here on its own: the same `Snackbar` that `JetnewsSnackbarHost` draws by default, with the
 * app's strings and brand colors. Whether the snackbar appears at all is behavior, checked on a
 * device by HomeScreenTests.postsContainError_snackbarShown.
 */
@Epic(VISUAL_REGRESSION_EPIC)
@Feature("Screens")
class FeedMessagesScreenshotTest {

    @get:Rule(order = 0)
    val attachSnapshotsOnFailure = AttachSnapshotsOnFailure()

    @get:Rule(order = 1)
    val paparazzi = componentPaparazzi()

    @Test
    @DisplayName("TR-30 Error snackbar of the home feed")
    @Story("Home feed states")
    @Severity(SeverityLevel.CRITICAL)
    @Description("The snackbar a failed refresh shows: 'Can't update latest news' with a Retry action, in the brand colors.")
    fun errorSnackbar_light() {
        paparazzi.snapshot {
            BrandTheme {
                ErrorSnackbar()
            }
        }
    }

    @Composable
    private fun ErrorSnackbar() {
        val visuals = FixedSnackbarVisuals(
            message = stringResource(R.string.load_error),
            actionLabel = stringResource(R.string.retry),
        )
        Snackbar(snackbarData = FixedSnackbarData(visuals))
    }

    /** The message and action the home feed passes to `showSnackbar` for a load error. */
    private class FixedSnackbarVisuals(override val message: String, override val actionLabel: String) : SnackbarVisuals {
        override val withDismissAction = false
        override val duration = SnackbarDuration.Indefinite
    }

    /** Snackbar data without a host, so the snackbar is drawn at once, without its enter animation. */
    private class FixedSnackbarData(override val visuals: SnackbarVisuals) : SnackbarData {
        override fun performAction() = Unit
        override fun dismiss() = Unit
    }
}
