package com.esec.examprep.domain.model

enum class ExamCategory(val storageKey: String) {
    GRADE_8("GRADE_8"),
    MATRICULATION("MATRICULATION");

    companion object {
        fun fromStorageKey(value: String?): ExamCategory =
            entries.firstOrNull { it.storageKey == value } ?: GRADE_8
    }
}
