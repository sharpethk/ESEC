package com.esec.examprep.domain.usecase

import com.esec.examprep.domain.model.Subject
import com.esec.examprep.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSubjectsUseCase @Inject constructor(
    private val repository: QuestionRepository,
) {
    operator fun invoke(): Flow<List<Subject>> = repository.getSubjects()
}
