package com.esec.examprep.presentation.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esec.examprep.domain.model.ExamCategory
import com.esec.examprep.domain.usecase.GetBookmarkedQuestionsUseCase
import com.esec.examprep.domain.usecase.GetSubjectsUseCase
import com.esec.examprep.domain.usecase.ToggleBookmarkUseCase
import com.esec.examprep.presentation.common.ActiveProfileHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val getBookmarked: GetBookmarkedQuestionsUseCase,
    private val getSubjects: GetSubjectsUseCase,
    private val toggleBookmark: ToggleBookmarkUseCase,
    private val activeProfile: ActiveProfileHolder,
) : ViewModel() {

    private val _state = MutableStateFlow(BookmarksState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            activeProfile.activeProfile
                .flatMapLatest { profile ->
                    if (profile == null) {
                        flowOf(emptyList())
                    } else {
                        combine(
                            getBookmarked(profile.id),
                            getSubjects(profile.examCategory),
                        ) { questions, subjects ->
                            val nameById = subjects.associate { it.id to it.name }
                            questions
                                .groupBy { it.subjectId }
                                .toSortedMap(compareBy { nameById[it] ?: it })
                                .map { (subjectId, qs) ->
                                    SubjectGroup(
                                        subjectId = subjectId,
                                        subjectName = nameById[subjectId] ?: subjectId,
                                        years = qs
                                            .groupBy { it.year }
                                            .toSortedMap(compareByDescending { it })
                                            .map { (year, list) -> YearGroup(year, list) },
                                    )
                                }
                        }
                    }
                }
                .collect { groups ->
                    _state.update { it.copy(groups = groups, isLoading = false) }
                }
        }
    }

    fun removeBookmark(questionId: String) {
        val profileId = activeProfile.activeProfile.value?.id ?: return
        viewModelScope.launch { toggleBookmark(profileId, questionId, false) }
    }
}
