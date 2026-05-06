package com.esec.examprep.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.esec.examprep.presentation.bookmarks.BookmarksScreen
import com.esec.examprep.presentation.dashboard.DashboardScreen
import com.esec.examprep.presentation.exam.ExamScreen
import com.esec.examprep.presentation.home.HomeScreen
import com.esec.examprep.presentation.result.ResultScreen
import com.esec.examprep.presentation.subject.SubjectScreen

private const val NAV_ANIM_MS = 350

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(NAV_ANIM_MS))
        },
        exitTransition = {
            fadeOut(tween(NAV_ANIM_MS / 2))
        },
        popEnterTransition = {
            fadeIn(tween(NAV_ANIM_MS / 2))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(NAV_ANIM_MS))
        },
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onSubjectsClick  = { navController.navigate(Screen.Subject.route) },
                onDashboardClick = { navController.navigate(Screen.Dashboard.route) },
                onBookmarksClick = { navController.navigate(Screen.Bookmarks.route) },
            )
        }

        composable(Screen.Subject.route) {
            SubjectScreen(
                onSubjectSelected = { subjectId, mode ->
                    navController.navigate(Screen.Exam().route(subjectId, mode))
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Screen.Exam().route,
            arguments = listOf(
                navArgument("subjectId") { type = NavType.StringType },
                navArgument("mode")      { type = NavType.StringType },
            ),
        ) { backStack ->
            val subjectId = backStack.arguments?.getString("subjectId").orEmpty()
            val mode      = backStack.arguments?.getString("mode").orEmpty()
            ExamScreen(
                subjectId = subjectId,
                mode      = mode,
                onFinished = { sessionId ->
                    navController.navigate(Screen.Result().route(sessionId)) {
                        popUpTo(Screen.Subject.route)
                    }
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Screen.Result().route,
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType }),
        ) { backStack ->
            val sessionId = backStack.arguments?.getString("sessionId").orEmpty()
            ResultScreen(
                sessionId = sessionId,
                onRetry   = { subjectId, mode ->
                    navController.navigate(Screen.Exam().route(subjectId, mode)) {
                        popUpTo(Screen.Subject.route)
                    }
                },
                onHome    = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Bookmarks.route) {
            BookmarksScreen(onBack = { navController.popBackStack() })
        }
    }
}
