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

import io.qameta.allure.Description
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Severity
import io.qameta.allure.SeverityLevel
import io.qameta.allure.junit4.DisplayName
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The TR ids live in two places: the `@DisplayName` of each test and the table in the
 * top-level README. This test fails when the two disagree, so they cannot drift apart.
 *
 * It finds every screenshot test by scanning the compiled classes of this package, so a new
 * test class is checked without being listed here.
 */
@Epic(VISUAL_REGRESSION_EPIC)
@Feature("Test catalog")
class TestCatalogConsistencyTest {

    @Test
    @DisplayName("Every TR id in the code matches the README table")
    @Severity(SeverityLevel.MINOR)
    @Description("Compares the TR id, test class and method of each test with the README table, in both directions.")
    fun readmeTableMatchesTheTests() {
        val inCode = testIdsInCode()
        val inReadme = testIdsInReadme()

        assertTrue("No screenshot tests with a TR id were found", inCode.isNotEmpty())
        assertEquals(
            "The README table and the @DisplayName ids differ. Update the table in README.md " +
                "(id -> TestClass.method).",
            inCode.toSortedMap(),
            inReadme.toSortedMap(),
        )
    }

    /** TR id -> "TestClass.method", from the `@DisplayName` of every test in this package. */
    private fun testIdsInCode(): Map<String, String> {
        val ids = mutableMapOf<String, String>()
        screenshotTestClasses().forEach { testClass ->
            testClass.declaredMethods
                .filter { it.isAnnotationPresent(Test::class.java) }
                .forEach { method ->
                    val name = method.getAnnotation(DisplayName::class.java)?.value.orEmpty()
                    val id = ID.find(name)?.value ?: return@forEach
                    val previous = ids.put(id, "${testClass.simpleName}.${method.name}")
                    check(previous == null) { "$id is used twice: $previous and ${testClass.simpleName}.${method.name}" }
                }
        }
        return ids
    }

    /** TR id -> "TestClass.method", from the rows of the README table. */
    private fun testIdsInReadme(): Map<String, String> {
        val ids = mutableMapOf<String, String>()
        readme().readLines().forEach { line ->
            val match = README_ROW.find(line) ?: return@forEach
            val (id, test) = match.destructured
            val previous = ids.put(id, test)
            check(previous == null) { "$id appears twice in the README table" }
        }
        return ids
    }

    /** Every class compiled into this package's test output folder. */
    private fun screenshotTestClasses(): List<Class<*>> {
        val packageName = javaClass.packageName
        val root = File(javaClass.protectionDomain.codeSource.location.toURI())
        val packageDir = File(root, packageName.replace('.', File.separatorChar))
        return packageDir.listFiles { file -> file.name.endsWith(".class") && '$' !in file.name }
            .orEmpty()
            // initialize = false: only annotations are read, no fixtures or rules are created.
            .map { Class.forName("$packageName.${it.nameWithoutExtension}", false, javaClass.classLoader) }
    }

    /** The top-level README. Gradle runs unit tests in the module folder, so search upwards. */
    private fun readme(): File = generateSequence(File("").absoluteFile) { it.parentFile }
        .map { File(it, "README.md") }
        .first { it.isFile && it.readText().contains("| TR-01") }

    private companion object {
        val ID = Regex("""^TR-\d+""")

        // | TR-01 | `FeedCardsScreenshotTest.topStoryCard_light` | ...
        val README_ROW = Regex("""^\|\s*(TR-\d+)\s*\|\s*`(\w+\.\w+)`""")
    }
}
