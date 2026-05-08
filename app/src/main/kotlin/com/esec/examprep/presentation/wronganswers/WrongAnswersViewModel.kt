package com.esec.examprep.presentation.wronganswers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esec.examprep.domain.repository.QuestionRepository
import com.esec.examprep.domain.usecase.GetSubjectsUseCase
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
class WrongAnswersViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val getSubjects: GetSubjectsUseCase,
    private val activeProfile: ActiveProfileHolder,
) : ViewModel() {

    private val _state = MutableStateFlow(WrongAnswersState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            activeProfile.activeProfile
                .flatMapLatest { profile ->
                    if (profile == null) {
                        flowOf(emptyList())
                    } else {
                        combine(
                            questionRepository.observeWrongAnswers(profile.id),
                            getSubjects(profile.examCategory),
                        ) { entries, subjects ->
                            val nameById = subjects.associate { it.id to it.name }
                            entries
                                .groupBy { it.question.subjectId }
                                .toSortedMap(compareBy { nameById[it] ?: it })
                                .map { (subjectId, list) ->
                                    WrongSubjectGroup(
                                        subjectId = subjectId,
                                        subjectName = nameById[subjectId] ?: subjectId,
                                        entries = list,
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
}
