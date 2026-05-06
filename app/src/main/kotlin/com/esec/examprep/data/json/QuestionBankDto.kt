package com.esec.examprep.data.json

import com.google.gson.annotations.SerializedName

/** Top-level shape of the decrypted questions_bank.json */
data class QuestionBankDto(
    @SerializedName("subjects") val subjects: List<SubjectDto>,
    @SerializedName("questions") val questions: List<QuestionDto>,
)

data class SubjectDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("category") val category: String,
)

data class QuestionDto(
    @SerializedName("id") val id: String,
    @SerializedName("subject_id") val subjectId: String,
    @SerializedName("year") val year: Int,
    @SerializedName("text") val text: String,
    @SerializedName("options") val options: List<OptionDto>,
    @SerializedName("correct_option_id") val correctOptionId: String,
    @SerializedName("explanation") val explanation: String?,
    @SerializedName("difficulty") val difficulty: String,
)

data class OptionDto(
    @SerializedName("id") val id: String,
    @SerializedName("text") val text: String,
)
