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

import com.example.jetnews.R
import com.example.jetnews.data.posts.impl.post1
import com.example.jetnews.data.posts.impl.post3
import com.example.jetnews.data.posts.impl.post4
import com.example.jetnews.data.posts.impl.post5
import com.example.jetnews.data.posts.impl.posts
import com.example.jetnews.model.BreakingNews
import com.example.jetnews.model.Post
import com.example.jetnews.model.PostAuthor
import com.example.jetnews.model.PostsFeed
import com.example.jetnews.ui.home.HomeUiState
import com.example.jetnews.utils.ErrorMessage

/**
 * Test data for the screenshot tests.
 *
 * Everything starts from the app's own hardcoded posts (data/posts/impl/PostsData.kt): fixed
 * dates, local drawables for images, no network. Edge cases are copies of real posts with
 * one field changed, so every other field still has the shape the app really uses.
 */
object ScreenshotFixtures {

    /** The feed the app shows on the home screen. */
    val feed: PostsFeed = posts

    /**
     * A headline made of long German compound words. Words this long cannot break at a
     * space, so the text must break inside a word and must not be clipped.
     */
    const val LONG_GERMAN_HEADLINE =
        "Bundesverfassungsgericht verhandelt über das Rechtsdurchsetzungsverbesserungsgesetz " +
            "und die Datenschutzgrundverordnungsumsetzung"

    val longGermanHeadlinePost: Post = post3.copy(title = LONG_GERMAN_HEADLINE)

    /** The breaking news the app shows above the feed. */
    val breakingNews: BreakingNews = checkNotNull(feed.breakingNews)

    /** Breaking news with a long German headline and summary, to check the 3- and 2-line limits. */
    val longGermanBreakingNews: BreakingNews = breakingNews.copy(
        headline = LONG_GERMAN_HEADLINE,
        summary = "Die Entscheidung betrifft Kommunalverwaltungen, Finanzämter und Gesundheitsämter " +
            "in allen sechzehn Bundesländern und soll ab dem kommenden Jahr schrittweise umgesetzt werden.",
    )

    /** The premium (paywalled) post from the recommended section of the feed. */
    val premiumPost: Post = feed.recommendedPosts.first { it.isPremium }

    /*
     * Premium versions of the posts the other card types show. The app's feed has no premium
     * post in these sections, but any card can show a paywalled article, so each card type is
     * checked for the badge.
     */
    val premiumTopPost: Post = feed.highlightedPost.copy(isPremium = true)
    val premiumPopularPost: Post = post5.copy(isPremium = true)
    val premiumHistoryPost: Post = post4.copy(isPremium = true)

    /** A post written in Arabic, for the right-to-left layout test. */
    val arabicPost: Post = post3.copy(
        title = "متحف جديد للفن الحديث يفتح أبوابه في برلين هذا الأسبوع",
        metadata = post3.metadata.copy(author = PostAuthor(name = "ليلى حسن")),
    )

    /** Home screen with posts loaded and one post bookmarked. */
    val homeWithPosts: HomeUiState.HasPosts = HomeUiState.HasPosts(
        postsFeed = feed,
        favorites = setOf(post1.id),
        isLoading = false,
        errorMessages = emptyList(),
        searchInput = "",
    )

    /** The error the view model reports when the feed cannot be refreshed. */
    val loadError: ErrorMessage = ErrorMessage(id = 0L, messageId = R.string.load_error)

    /** First start: no posts yet, the feed is loading. */
    val homeInitialLoading: HomeUiState = HomeUiState.NoPosts(
        isLoading = true,
        errorMessages = emptyList(),
        searchInput = "",
    )

    /** Pull to refresh in progress: the old posts stay visible under the refresh indicator. */
    val homeRefreshing: HomeUiState = homeWithPosts.copy(isLoading = true)

    /** The first load failed: no posts, only the snackbar with a retry. */
    val homeLoadFailed: HomeUiState = HomeUiState.NoPosts(
        isLoading = false,
        errorMessages = listOf(loadError),
        searchInput = "",
    )

    /** Home screen with no posts, not loading and no error: the "Tap to load content" state. */
    val homeEmpty: HomeUiState = HomeUiState.NoPosts(
        isLoading = false,
        errorMessages = emptyList(),
        searchInput = "",
    )
}
