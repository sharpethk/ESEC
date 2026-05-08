package com.esec.examprep.presentation.navigation

sealed class Screen(val route: String) {
    data object Home       : Screen("home")
    data object Subject    : Screen("subject")
    data object Dashboard  : Screen("dashboard")
    data object Bookmarks  : Screen("bookmarks")
    data object Settings   : Screen("settings")
    data object ProfilePicker : Screen("profile_picker")
    data object WrongAnswers : Screen("wrong_answers")
    data object PracticeBuilder : Screen("practice_builder")
    data object DailyChallenge : Screen("daily_challenge")
    data object Achievements : Screen("achievements")
    data object ParentGate : Screen("parent_gate")
    data object ParentView : Screen("parent_view")

    data class ProfileEdit(
        val profileId: String = "{profileId}",
    ) : Screen("profile_edit?profileId={profileId}") {
        fun route(profileId: String? = null): String =
            if (profileId.isNullOrEmpty()) "profile_edit" else "profile_edit?profileId=$profileId"
    }

    data class Exam(
        val subjectId: String = "{subjectId}",
        val mode: String = "{mode}",
    ) : Screen("exam/{subjectId}/{mode}?year={year}") {
        fun route(subjectId: String, mode: String, year: Int? = null): String {
            val base = "exam/$subjectId/$mode"
            return if (year != null && year > 0) "$base?year=$year" else base
        }
    }

    data class Result(
        val sessionId: String = "{sessionId}",
    ) : Screen("result/{sessionId}") {
        fun route(sessionId: String) = "result/$sessionId"
    }

    data class QuestionDetail(
        val questionId: String = "{questionId}",
    ) : Screen("question/{questionId}") {
        fun route(questionId: String) = "question/$questionId"
    }
}
