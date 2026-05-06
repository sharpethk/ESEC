package com.esec.examprep.presentation.bookmarks

import com.esec.examprep.domain.model.DifficultyLevel
import com.esec.examprep.domain.model.Option
import com.esec.examprep.domain.model.Question
import com.esec.examprep.domain.usecase.GetBookmarkedQuestionsUseCase
import com.esec.examprep.domain.usecase.ToggleBookmarkUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarksViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val getBookmarked: GetBookmarkedQuestionsUseCase = mockk()
    private val toggleBookmark: ToggleBookmarkUseCase = mockk(relaxed = true)
    private val flow = MutableStateFlow<List<Question>>(emptyList())

    private val q = Question(
        id = "q1", subjectId = "sub", year = 2024, text = "Q?",
        options = listOf(Option("a", "A"), Option("b", "B")),
        correctOptionId = "a", explanation = null, difficultyLevel = DifficultyLevel.EASY,
        isBookmarked = true,
    )

    @Before fun setup() {
        Dispatchers.setMain(dispatcher)
        every { getBookmarked() } returns flow
    }
    @After fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun `state mirrors bookmarked questions flow`() = runTest {
        flow.value = listOf(q)
        val vm = BookmarksViewModel(getBookmarked, toggleBookmark)
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, vm.state.value.questions.size)
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun `removeBookmark calls toggle with false`() = runTest {
        val vm = BookmarksViewModel(getBookmarked, toggleBookmark)
        dispatcher.scheduler.advanceUntilIdle()
        vm.removeBookmark("q1")
        dispatcher.scheduler.advanceUntilIdle()
        coVerify { toggleBookmark("q1", false) }
    }
}
