package com.esec.examprep.presentation.dashboard

import com.esec.examprep.domain.model.UserProgress
import com.esec.examprep.domain.model.WeakTopic
import com.esec.examprep.domain.repository.ExamSessionRepository
import com.esec.examprep.domain.usecase.GetProgressUseCase
import com.esec.examprep.domain.usecase.GetRecentExamsUseCase
import com.esec.examprep.domain.usecase.GetTimeStatsUseCase
import com.esec.examprep.domain.usecase.GetWeakTopicsUseCase
import io.mockk.coEvery
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
class DashboardViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val getProgress: GetProgressUseCase = mockk()
    private val getWeakTopics: GetWeakTopicsUseCase = mockk()
    private val getRecent: GetRecentExamsUseCase = mockk()
    private val getTime: GetTimeStatsUseCase = mockk()
    private val repo: ExamSessionRepository = mockk(relaxUnitFun = true)

    private val progressFlow = MutableStateFlow<List<UserProgress>>(emptyList())
    private val weakFlow = MutableStateFlow<List<WeakTopic>>(emptyList())

    @Before fun setup() {
        Dispatchers.setMain(dispatcher)
        every { getProgress() } returns progressFlow
        every { getWeakTopics() } returns weakFlow
        coEvery { getRecent(any()) } returns emptyList()
        coEvery { getTime() } returns 0.0
    }
    @After fun tearDown() { Dispatchers.resetMain() }

    private fun build() = DashboardViewModel(getProgress, getWeakTopics, getRecent, getTime, repo)

    @Test
    fun `combines progress and weak topics into state`() = runTest {
        progressFlow.value = listOf(
            UserProgress("sub1", "Sub 1", 3, 90f, 75f, 30, 22),
        )
        weakFlow.value = listOf(WeakTopic("sub1", 0.4f, 10))

        val vm = build()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, vm.state.value.progress.size)
        assertEquals(1, vm.state.value.weakTopics.size)
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun `loads recent and time stats on init`() = runTest {
        coEvery { getTime() } returns 42.5
        val vm = build()
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(42.5, vm.state.value.avgTimePerQuestion, 0.01)
    }

    @Test
    fun `clearAllProgress delegates to repo`() = runTest {
        val vm = build()
        dispatcher.scheduler.advanceUntilIdle()
        vm.clearAllProgress()
        dispatcher.scheduler.advanceUntilIdle()
        coVerify { repo.clearAllProgress() }
        assertEquals(0.0, vm.state.value.avgTimePerQuestion, 0.01)
    }
}
