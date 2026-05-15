package com.esec.examprep.presentation

import com.esec.examprep.data.preferences.UserPreferences
import com.esec.examprep.data.preferences.UserPreferencesRepository
import com.esec.examprep.domain.model.ExamCategory
import com.esec.examprep.domain.model.Profile
import com.esec.examprep.domain.repository.ProfileRepository
import com.esec.examprep.domain.usecase.EnsureDataLoadedUseCase
import com.esec.examprep.presentation.navigation.Screen
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import javax.crypto.BadPaddingException

/**
 * Exhaustive crash-path coverage for [MainViewModel.init]. Every collaborator
 * is mocked so the test is pure-JVM, deterministic and uses virtual time.
 *
 * The MainViewModel's init block must:
 *   1. Never let `_startDestination` stay null (the splash would hang forever).
 *   2. Survive any combination of decryption / Room / DataStore / repo failures.
 *   3. Respect the 25s timeout ceiling around `ensureDataLoaded()`.
 *   4. Route correctly based on profile count + active profile + PIN state.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelInitTest {

    private val dispatcher = StandardTestDispatcher()
    private val prefs: UserPreferencesRepository = mockk()
    private val profileRepo: ProfileRepository = mockk(relaxUnitFun = true)
    private val ensureDataLoaded: EnsureDataLoadedUseCase = mockk()
    private val prefsFlow = MutableStateFlow(UserPreferences())

    @Before fun setUp() {
        Dispatchers.setMain(dispatcher)
        every { prefs.preferences } returns prefsFlow
    }

    @After fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun profile(id: String, hasPin: Boolean = false) = Profile(
        id = id, name = "P-$id", avatarKey = "avatar_owl", gradeLevel = 8,
        examCategory = ExamCategory.GRADE_8, hasPin = hasPin,
        createdAt = 0L, lastActiveAt = 0L,
    )

    private fun build(): MainViewModel = MainViewModel(prefs, profileRepo, ensureDataLoaded)

    // ---------------------------------------------------------------------
    // 1. Happy path
    // ---------------------------------------------------------------------
    @Test fun `happy path single profile routes to Home and auto-selects it`() = runTest {
        coEvery { ensureDataLoaded.invoke() } returns Unit
        val only = profile("p1")
        every { profileRepo.observeProfiles() } returns MutableStateFlow(listOf(only))
        coEvery { profileRepo.getActiveProfile() } returns null
        coEvery { profileRepo.setActiveProfile(any()) } returns Unit

        val vm = build()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(Screen.Home.route, vm.startDestination.value)
        coVerify(exactly = 1) { profileRepo.setActiveProfile("p1") }
    }

    // ---------------------------------------------------------------------
    // 2. Decryption failures during ensureDataLoaded
    // ---------------------------------------------------------------------
    @Test fun `BadPadding from decryptor does not crash init`() = runTest {
        coEvery { ensureDataLoaded.invoke() } throws BadPaddingException("bad bank")
        every { profileRepo.observeProfiles() } returns MutableStateFlow(emptyList())
        coEvery { profileRepo.getActiveProfile() } returns null

        val vm = build()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(Screen.Home.route, vm.startDestination.value)
    }

    @Test fun `missing asset IOException does not crash init`() = runTest {
        coEvery { ensureDataLoaded.invoke() } throws java.io.IOException("asset missing")
        every { profileRepo.observeProfiles() } returns MutableStateFlow(emptyList())
        coEvery { profileRepo.getActiveProfile() } returns null

        val vm = build()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(Screen.Home.route, vm.startDestination.value)
    }

    @Test fun `OutOfMemoryError from data load does not crash init`() = runTest {
        coEvery { ensureDataLoaded.invoke() } throws OutOfMemoryError("oom")
        every { profileRepo.observeProfiles() } returns MutableStateFlow(emptyList())
        coEvery { profileRepo.getActiveProfile() } returns null

        val vm = build()
        dispatcher.scheduler.advanceUntilIdle()

        // OutOfMemoryError is a Throwable — outer try/catch fallback path.
        assertEquals(Screen.Home.route, vm.startDestination.value)
    }

    @Test fun `generic RuntimeException from data load does not crash init`() = runTest {
        coEvery { ensureDataLoaded.invoke() } throws IllegalStateException("boom")
        every { profileRepo.observeProfiles() } returns MutableStateFlow(emptyList())
        coEvery { profileRepo.getActiveProfile() } returns null

        val vm = build()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(Screen.Home.route, vm.startDestination.value)
    }

    // ---------------------------------------------------------------------
    // 3. Timeout path — ensureDataLoaded suspends forever
    // ---------------------------------------------------------------------
    @Test fun `ensureDataLoaded suspending forever still releases via 25s ceiling`() = runTest {
        coEvery { ensureDataLoaded.invoke() } coAnswers { awaitCancellation() }
        every { profileRepo.observeProfiles() } returns MutableStateFlow(emptyList())
        coEvery { profileRepo.getActiveProfile() } returns null

        val vm = build()
        // Not yet past the timeout — destination should still be null.
        dispatcher.scheduler.advanceTimeBy(10_000)
        assertEquals(null, vm.startDestination.value)
        // Past the 25s ceiling — init continues and resolves to Home.
        dispatcher.scheduler.advanceTimeBy(20_000)
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(Screen.Home.route, vm.startDestination.value)
    }

    // ---------------------------------------------------------------------
    // 4. ProfileRepository failures
    // ---------------------------------------------------------------------
    @Test fun `observeProfiles throwing does not crash init`() = runTest {
        coEvery { ensureDataLoaded.invoke() } returns Unit
        every { profileRepo.observeProfiles() } returns flow { throw IllegalStateException("db dead") }
        coEvery { profileRepo.getActiveProfile() } returns null

        val vm = build()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(Screen.Home.route, vm.startDestination.value)
    }

    @Test fun `observeProfiles emitting empty list routes to Home`() = runTest {
        coEvery { ensureDataLoaded.invoke() } returns Unit
        every { profileRepo.observeProfiles() } returns MutableStateFlow(emptyList())
        coEvery { profileRepo.getActiveProfile() } returns null

        val vm = build()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(Screen.Home.route, vm.startDestination.value)
        // Should NOT auto-select anything when list is empty.
        coVerify(exactly = 0) { profileRepo.setActiveProfile(any()) }
    }

    // ---------------------------------------------------------------------
    // 5. getActiveProfile throws
    // ---------------------------------------------------------------------
    @Test fun `getActiveProfile throwing is treated as null active`() = runTest {
        coEvery { ensureDataLoaded.invoke() } returns Unit
        every { profileRepo.observeProfiles() } returns MutableStateFlow(
            listOf(profile("p1"), profile("p2"))
        )
        coEvery { profileRepo.getActiveProfile() } throws RuntimeException("prefs corrupted")

        val vm = build()
        dispatcher.scheduler.advanceUntilIdle()

        // 2 profiles + null active → ProfilePicker route.
        assertEquals(Screen.ProfilePicker.route, vm.startDestination.value)
    }

    // ---------------------------------------------------------------------
    // 6. setActiveProfile throws (single profile branch)
    // ---------------------------------------------------------------------
    @Test fun `setActiveProfile throwing on single profile still resolves destination`() = runTest {
        coEvery { ensureDataLoaded.invoke() } returns Unit
        every { profileRepo.observeProfiles() } returns MutableStateFlow(listOf(profile("p1")))
        coEvery { profileRepo.getActiveProfile() } returns null
        coEvery { profileRepo.setActiveProfile(any()) } throws IllegalStateException("write fail")

        val vm = build()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(Screen.Home.route, vm.startDestination.value)
    }

    // ---------------------------------------------------------------------
    // 7. Multiple profiles + active has PIN → ProfilePicker
    // ---------------------------------------------------------------------
    @Test fun `multiple profiles with PIN-protected active routes to ProfilePicker`() = runTest {
        coEvery { ensureDataLoaded.invoke() } returns Unit
        val a = profile("a", hasPin = true)
        val b = profile("b")
        every { profileRepo.observeProfiles() } returns MutableStateFlow(listOf(a, b))
        coEvery { profileRepo.getActiveProfile() } returns a

        val vm = build()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(Screen.ProfilePicker.route, vm.startDestination.value)
    }

    // ---------------------------------------------------------------------
    // 8. Multiple profiles + active no PIN → Home
    // ---------------------------------------------------------------------
    @Test fun `multiple profiles with unprotected active routes to Home`() = runTest {
        coEvery { ensureDataLoaded.invoke() } returns Unit
        val a = profile("a", hasPin = false)
        val b = profile("b")
        every { profileRepo.observeProfiles() } returns MutableStateFlow(listOf(a, b))
        coEvery { profileRepo.getActiveProfile() } returns a

        val vm = build()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(Screen.Home.route, vm.startDestination.value)
        // 2 profiles → should NOT auto-select.
        coVerify(exactly = 0) { profileRepo.setActiveProfile(any()) }
    }

    // ---------------------------------------------------------------------
    // 9. Cancellation safety — no uncaught exception escapes
    // ---------------------------------------------------------------------
    @Test fun `CancellationException from observeProfiles does not break destination`() = runTest {
        coEvery { ensureDataLoaded.invoke() } returns Unit
        every { profileRepo.observeProfiles() } returns flow {
            throw CancellationException("scope cancelled mid-init")
        }
        coEvery { profileRepo.getActiveProfile() } returns null

        val vm = build()
        dispatcher.scheduler.advanceUntilIdle()

        // Whether the outer try/catch swallows the CE or the inner runCatching
        // catches it, the splash must never hang — destination must be set.
        assertNotNull(vm.startDestination.value)
    }

    // ---------------------------------------------------------------------
    // 10. Catastrophic — all collaborators throw
    // ---------------------------------------------------------------------
    @Test fun `all collaborators throwing still falls back to Home via outer try-catch`() = runTest {
        coEvery { ensureDataLoaded.invoke() } throws Error("vm-init catastrophe")
        every { profileRepo.observeProfiles() } returns flow { throw RuntimeException("db") }
        coEvery { profileRepo.getActiveProfile() } throws RuntimeException("prefs")
        coEvery { profileRepo.setActiveProfile(any()) } throws RuntimeException("write")

        val vm = build()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(Screen.Home.route, vm.startDestination.value)
    }
}
