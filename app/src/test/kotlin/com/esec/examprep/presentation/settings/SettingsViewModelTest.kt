package com.esec.examprep.presentation.settings

import com.esec.examprep.data.preferences.ThemeMode
import com.esec.examprep.data.preferences.UserPreferences
import com.esec.examprep.data.preferences.UserPreferencesRepository
import com.esec.examprep.domain.model.ExamCategory
import com.esec.examprep.domain.model.Profile
import com.esec.examprep.domain.repository.ExamSessionRepository
import com.esec.examprep.domain.repository.ProfileRepository
import com.esec.examprep.domain.usecase.ReloadQuestionBankUseCase
import com.esec.examprep.presentation.common.ActiveProfileHolder
import com.esec.examprep.work.DailyReminderScheduler
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val prefs: UserPreferencesRepository = mockk(relaxUnitFun = true)
    private val examRepo: ExamSessionRepository = mockk(relaxUnitFun = true)
    private val profileRepo: ProfileRepository = mockk()
    private val reloadQuestionBank: ReloadQuestionBankUseCase = mockk(relaxUnitFun = true)
    private val reminderScheduler: DailyReminderScheduler = mockk(relaxUnitFun = true)
    private val prefsFlow = MutableStateFlow(UserPreferences())

    private val testProfile = Profile(
        id = "p1", name = "Test", avatarKey = "avatar_owl", gradeLevel = 8,
        examCategory = ExamCategory.GRADE_8, hasPin = false, createdAt = 0, lastActiveAt = 0,
    )

    @Before fun setup() {
        Dispatchers.setMain(dispatcher)
        every { prefs.preferences } returns prefsFlow
        every { profileRepo.observeActiveProfile() } returns MutableStateFlow<Profile?>(testProfile)
    }
    @After fun tearDown() { Dispatchers.resetMain() }

    private fun build(): SettingsViewModel {
        val holder = ActiveProfileHolder(profileRepo)
        return SettingsViewModel(prefs, examRepo, holder, reloadQuestionBank, reminderScheduler)
    }

    @Test
    fun `state reflects preferences flow updates`() = runTest {
        val vm = build()
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(UserPreferences(), vm.state.value.preferences)
        assertFalse(vm.state.value.isLoading)

        prefsFlow.value = UserPreferences(themeMode = ThemeMode.DARK, defaultExamLength = 20)
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(ThemeMode.DARK, vm.state.value.preferences.themeMode)
        assertEquals(20, vm.state.value.preferences.defaultExamLength)
    }

    @Test
    fun `onThemeModeChanged delegates to repo`() = runTest {
        val vm = build()
        vm.onThemeModeChanged(ThemeMode.LIGHT)
        dispatcher.scheduler.advanceUntilIdle()
        coVerify { prefs.setThemeMode(ThemeMode.LIGHT) }
    }

    @Test
    fun `onExamLengthChanged delegates to repo`() = runTest {
        val vm = build()
        vm.onExamLengthChanged(60)
        dispatcher.scheduler.advanceUntilIdle()
        coVerify { prefs.setExamLength(60) }
    }

    @Test
    fun `clearHistoryConfirmed clears progress and dismisses dialog`() = runTest {
        coEvery { examRepo.clearAllProgress(any()) } returns Unit
        val vm = build()
        dispatcher.scheduler.advanceUntilIdle()
        vm.showClearHistoryDialog(true)
        assertTrue(vm.state.value.showClearHistoryDialog)

        vm.clearHistoryConfirmed()
        dispatcher.scheduler.advanceUntilIdle()

        coVerify { examRepo.clearAllProgress(any()) }
        assertFalse(vm.state.value.showClearHistoryDialog)
    }
}
