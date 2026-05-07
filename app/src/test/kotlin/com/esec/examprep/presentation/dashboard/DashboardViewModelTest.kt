package com.esec.examprep.presentation.dashboard

import com.esec.examprep.domain.model.ExamCategory
import com.esec.examprep.domain.model.Profile
import com.esec.examprep.domain.model.UserProgress
import com.esec.examprep.domain.model.WeakTopic
import com.esec.examprep.domain.repository.ExamSessionRepository
import com.esec.examprep.domain.repository.ProfileRepository
import com.esec.examprep.domain.usecase.GetProgressUseCase
import com.esec.examprep.domain.usecase.GetRecentExamsUseCase
import com.esec.examprep.domain.usecase.GetTimeStatsUseCase
import com.esec.examprep.domain.usecase.GetWeakTopicsUseCase
import com.esec.examprep.presentation.common.ActiveProfileHolder
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
    private val profileRepo: ProfileRepository = mockk()

    private val progressFlow = MutableStateFlow<List<UserProgress>>(emptyList())
    private val weakFlow = MutableStateFlow<List<WeakTopic>>(emptyList())

    private val testProfile = Profile(
        id = "p1", name = "Test", avatarKey = "avatar_owl", gradeLevel = 8,
        examCategory = ExamCategory.GRADE_8, hasPin = false, createdAt = 0, lastActiveAt = 0,
    )
    private val activeProfileFlow = MutableStateFlow<Profile?>(testProfile)

    @Before fun setup() {
        Dispatchers.setMain(dispatcher)
        every { profileRepo.observeActiveProfile() } returns activeProfileFlow
        every { getProgress(any()) } returns progressFlow
        every { getWeakTopics(any()) } returns weakFlow
        coEvery { getRecent(any(), any()) } returns emptyList()
        coEvery { getTime(any()) } returns 0.0
    }
    @After fun tearDown() { Dispatchers.resetMain() }

    private fun build(): DashboardViewModel {
        val holder = ActiveProfileHolder(profileRepo)
        return DashboardViewModel(getProgress, getWeakTopics, getRecent, getTime, repo, holder)
    }

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
        coEvery { getTime(any()) } returns 42.5
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
        coVerify { repo.clearAllProgress(any()) }
        assertEquals(0.0, vm.state.value.avgTimePerQuestion, 0.01)
    }
}
