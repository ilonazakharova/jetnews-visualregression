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

import io.qameta.allure.Allure
import java.io.File
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * When a snapshot comparison fails, attaches the approved golden, the actual rendering and
 * Paparazzi's delta image to the failing test in the Allure report.
 *
 * Only files that belong to the failing test are attached. Passed tests get no attachments.
 *
 * Declare this rule with a lower `order` than the Paparazzi rule, so it wraps it and runs
 * after Paparazzi has written its failure files.
 */
class AttachSnapshotsOnFailure : TestWatcher() {

    override fun failed(e: Throwable, description: Description) {
        // Paparazzi names every file <package>_<TestClass>_<testMethod>.png.
        val suffix = "_${description.testClass.simpleName}_${description.methodName}.png"

        System.getProperty("paparazzi.snapshot.dir")
            ?.let { File(it, "images") }
            .filesEndingWith(suffix)
            .forEach { attach("1. Approved golden", it) }

        val failures = System.getProperty("paparazzi.failures.dir")?.let(::File).filesEndingWith(suffix)
        failures.filterNot { it.name.startsWith(DELTA_PREFIX) }.forEach { attach("2. Actual rendering", it) }
        failures.filter { it.name.startsWith(DELTA_PREFIX) }.forEach { attach("3. Delta (golden | diff | actual)", it) }
    }

    private fun File?.filesEndingWith(suffix: String): List<File> =
        this?.listFiles { file -> file.isFile && file.name.endsWith(suffix) }?.sortedBy { it.name }.orEmpty()

    private fun attach(name: String, file: File) {
        file.inputStream().use { Allure.addAttachment(name, "image/png", it, "png") }
    }

    private companion object {
        const val DELTA_PREFIX = "delta-"
    }
}
