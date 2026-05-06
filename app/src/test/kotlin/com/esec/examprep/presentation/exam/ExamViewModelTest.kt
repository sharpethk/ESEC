package com.esec.examprep.presentation.exam

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.esec.examprep.data.preferences.UserPreferences
import com.esec.examprep.data.preferences.UserPreferencesRepository
import com.esec.examprep.domain.model.DifficultyLevel
import com.esec.examprep.domain.model.ExamMode
import com.esec.examprep.domain.model.Option
import com.esec.examprep.domain.model.Question
import com.esec.examprep.domain.usecase.GetQuestionsForExamUseCase
import com.esec.examprep.domain.usecase.SubmitExamUseCase
import com.esec.examprep.domain.usecase.ToggleBookmarkUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExamViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val getQuestions: GetQuestionsForExamUseCase = mockk()
    private val submitExam: SubmitExamUseCase = mockk()
    private val toggleBookmark: ToggleBookmarkUseCase = mockk(relaxed = true)
    private val prefsRepo: UserPreferencesRepository = mockk()

    private val sampleQuestion = Question(
        id = "q1", subjectId = "sub", year = 2024, text = "Sample?",
        options = listOf(Option("a", "A"), Option("b", "B")),
        correctOptionId = "a", explanation = null, difficultyLevel = DifficultyLevel.EASY,
    )

    @Before fun setup() {
        Dispatchers.setMain(dispatcher)
        every { prefsRepo.preferences } returns flowOf(UserPreferences())
    }
    @After  fun tearDown() { Dispatchers.resetMain() }

    private fun buildViewModel(mode: String = ExamMode.PRACTICE.name): ExamViewModel {
        val handle = SavedStateHandle(mapOf("subjectId" to "sub", "mode" to mode))
        return ExamViewModel(handle, getQuestions, submitExam, toggleBookmark, prefsRepo)
    }

    @Test
    fun `selecting an answer updates state answers map`() = runTest {
        coEvery { getQuestions(any(), any()) } returns listOf(sampleQuestion)

        val vm = buildViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        vm.state.test {
            val loaded = awaitItem()
            assertEquals(0, loaded.answers.size)

            vm.selectAnswer("a")
            val answered = awaitItem()
            assertEquals("a", answered.answers["q1"])
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `nextQuestion increments currentIndex`() = runTest {
        val q2 = sampleQuestion.copy(id = "q2")
        coEvery { getQuestions(any(), any()) } returns listOf(sampleQuestion, q2)

        val vm = buildViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        vm.nextQuestion()
        assertEquals(1, vm.state.value.currentIndex)
    }
}
