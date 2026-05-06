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
    ) : Screen("exam/{subjectId}/{mode}") {
        fun route(subjectId: String, mode: String) = "exam/$subjectId/$mode"
    }

    data class Result(
        val sessionId: String = "{sessionId}",
    ) : Screen("result/{sessionId}") {
        fun route(sessionId: String) = "result/$sessionId"
    }
}
