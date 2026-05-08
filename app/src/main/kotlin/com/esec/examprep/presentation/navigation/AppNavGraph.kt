package com.esec.examprep.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.esec.examprep.presentation.achievements.AchievementsScreen
import com.esec.examprep.presentation.bookmarks.BookmarksScreen
import com.esec.examprep.presentation.dashboard.DashboardScreen
import com.esec.examprep.presentation.exam.ExamScreen
import com.esec.examprep.presentation.home.HomeScreen
import com.esec.examprep.presentation.parent.ParentGateScreen
import com.esec.examprep.presentation.parent.ParentViewScreen
import com.esec.examprep.presentation.practice.PracticeBuilderScreen
import com.esec.examprep.presentation.profile.ProfileEditScreen
import com.esec.examprep.presentation.profile.ProfilePickerScreen
import com.esec.examprep.presentation.questiondetail.QuestionDetailScreen
import com.esec.examprep.presentation.result.ResultScreen
import com.esec.examprep.presentation.settings.SettingsScreen
import com.esec.examprep.presentation.subject.SubjectScreen
import com.esec.examprep.presentation.wronganswers.WrongAnswersScreen

private const val NAV_ANIM_MS = 350

private data class TopTab(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

private val topTabs = listOf(
    TopTab(Screen.Home.route, "Home", Icons.Default.Home),
    TopTab(Screen.Subject.route, "Exams", Icons.AutoMirrored.Filled.MenuBook),
    TopTab(Screen.Bookmarks.route, "Bookmarks", Icons.Default.Bookmark),
    TopTab(Screen.Settings.route, "Settings", Icons.Default.Settings),
)

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route,
) {
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = topTabs.any { it.route == currentRoute }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                ) {
                    topTabs.forEach { tab ->
                        val selected = backStack?.destination?.hierarchy
                            ?.any { it.route == tab.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != tab.route) {
                                    navController.navigate(tab.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            ),
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize().padding(padding),
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
                    onSubjectsClick  = {
                        navController.navigate(Screen.Subject.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onDashboardClick = { navController.navigate(Screen.Dashboard.route) },
                    onStartDailyChallenge = {
                        navController.navigate(Screen.Exam().route("daily", "DAILY"))
                    },
                )
            }

            composable(Screen.Subject.route) {
                SubjectScreen(
                    onSubjectSelected = { subjectId, mode, year ->
                        navController.navigate(Screen.Exam().route(subjectId, mode.name, year))
                    },
                    onBack = { navController.popBackStack() },
                )
            }

            composable(
                route = Screen.Exam().route,
                arguments = listOf(
                    navArgument("subjectId") { type = NavType.StringType },
                    navArgument("mode")      { type = NavType.StringType },
                    navArgument("year")      {
                        type = NavType.IntType
                        defaultValue = -1
                    },
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
                BookmarksScreen(
                    onBack = { navController.popBackStack() },
                    onQuestionClick = { questionId ->
                        navController.navigate(Screen.QuestionDetail().route(questionId))
                    },
                    onWrongAnswersClick = {
                        navController.navigate(Screen.WrongAnswers.route)
                    },
                )
            }

            composable(Screen.WrongAnswers.route) {
                WrongAnswersScreen(
                    onBack = { navController.popBackStack() },
                    onQuestionClick = { questionId ->
                        navController.navigate(Screen.QuestionDetail().route(questionId))
                    },
                    onReviewAll = {
                        navController.navigate(Screen.Exam().route("all", "REVIEW"))
                    },
                    onReviewSubject = { subjectId ->
                        navController.navigate(Screen.Exam().route(subjectId, "REVIEW"))
                    },
                )
            }

            composable(Screen.PracticeBuilder.route) {
                PracticeBuilderScreen(
                    onBack = { navController.popBackStack() },
                    onStart = {
                        navController.navigate(Screen.Exam().route("custom", "PRACTICE_CUSTOM")) {
                            popUpTo(Screen.PracticeBuilder.route) { inclusive = true }
                        }
                    },
                )
            }

            composable(
                route = Screen.QuestionDetail().route,
                arguments = listOf(navArgument("questionId") { type = NavType.StringType }),
            ) {
                QuestionDetailScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onManageProfiles = { navController.navigate(Screen.ProfilePicker.route) },
                    onPracticeBuilder = { navController.navigate(Screen.PracticeBuilder.route) },
                    onAchievements = { navController.navigate(Screen.Achievements.route) },
                    onParentView = { navController.navigate(Screen.ParentGate.route) },
                )
            }

            composable(Screen.Achievements.route) {
                AchievementsScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.ParentGate.route) {
                ParentGateScreen(
                    onBack = { navController.popBackStack() },
                    onUnlocked = {
                        navController.navigate(Screen.ParentView.route) {
                            popUpTo(Screen.ParentGate.route) { inclusive = true }
                        }
                    },
                )
            }

            composable(Screen.ParentView.route) {
                ParentViewScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.ProfilePicker.route) {
                ProfilePickerScreen(
                    onProfilePicked = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onAddProfile = { navController.navigate(Screen.ProfileEdit().route(null)) },
                )
            }

            composable(
                route = Screen.ProfileEdit().route,
                arguments = listOf(
                    navArgument("profileId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                ),
            ) {
                ProfileEditScreen(
                    onDone = { navController.popBackStack() },
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
