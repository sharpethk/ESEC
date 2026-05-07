package com.esec.examprep.presentation.navigation

sealed class Screen(val route: String) {
    data object Home       : Screen("home")
    data object Subject    : Screen("subject")
    data object Dashboard  : Screen("dashboard")
    data object Bookmarks  : Screen("bookmarks")
    data object Settings   : Screen("settings")

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
}
