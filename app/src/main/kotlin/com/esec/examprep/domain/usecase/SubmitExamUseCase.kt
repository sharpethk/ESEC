package com.esec.examprep.domain.usecase

import com.esec.examprep.domain.model.ExamResult
import com.esec.examprep.domain.model.ExamSession
import com.esec.examprep.domain.repository.ExamSessionRepository
import java.time.Instant
import javax.inject.Inject

class SubmitExamUseCase @Inject constructor(
    private val sessionRepository: ExamSessionRepository,
) {
    suspend operator fun invoke(session: ExamSession): ExamResult {
        val finished = session.copy(finishedAt = Instant.now())
        return sessionRepository.saveSession(finished)
    }
}
