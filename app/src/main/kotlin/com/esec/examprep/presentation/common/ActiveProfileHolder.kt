package com.esec.examprep.presentation.common

import com.esec.examprep.domain.model.Profile
import com.esec.examprep.domain.repository.ProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActiveProfileHolder @Inject constructor(
    profileRepository: ProfileRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val activeProfile: StateFlow<Profile?> =
        profileRepository.observeActiveProfile()
            .stateIn(scope, SharingStarted.Eagerly, null)
}
