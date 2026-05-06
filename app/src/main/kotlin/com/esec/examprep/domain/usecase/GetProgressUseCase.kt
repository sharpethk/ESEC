package com.esec.examprep.domain.usecase

import com.esec.examprep.domain.model.UserProgress
import com.esec.examprep.domain.repository.ExamSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProgressUseCase @Inject constructor(
    private val repository: ExamSessionRepository,
) {
    operator fun invoke(): Flow<List<UserProgress>> = repository.getProgressBySubject()
}
