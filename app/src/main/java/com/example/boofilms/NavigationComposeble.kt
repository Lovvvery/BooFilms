package com.example.boofilms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AuthNavigation(authManager: AuthManager) {
    val navController = rememberNavController()
    val isLoggedIn by remember { derivedStateOf { authManager.isLoggedIn() } }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "main" else "login"
    ) {
        composable("login") {
            LoginScreen(
                authManager = authManager,
                onLoginSuccess = {
                    navController.navigate("main") {
                        // Очищаем back stack до экрана входа
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate("registration") },
                onGuestLogin = { // Добавлен гостевой вход
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("registration") {
            RegistrationScreen(
                authManager = authManager,
                onRegisterSuccess = {
                    navController.navigate("main") {
                        popUpTo("registration") { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() },
                onLoginClick = { navController.navigate("login") }
            )
        }

        composable("main") {
            MainScreen(
                authManager = authManager,
                onNavigateToCatalog = { navController.navigate("catalog") },
                onNavigateToAccount = { navController.navigate("account") },
                onNavigateToSettings = { navController.navigate("settings") },
                onLogout = {
                    authManager.logout()
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
            )
        }

        composable("catalog") {
            val viewModel: MovieViewModel = hiltViewModel()

            val movies by viewModel.movies.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()

            LaunchedEffect(Unit) {
                if (movies.isEmpty()) {
                    viewModel.loadMovies()
                }
            }
        }

        composable(
            "filmDetails/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
            MovieDetailsScreen(movieId = movieId)
        }

        composable("account") {
            AccountScreen(
                authManager = authManager,
                onBackClick = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }

        composable("settings") {
            SettingsScreen(
                authManager = authManager,
                onBackClick = { navController.popBackStack() },
                onLogout = {
                    authManager.logout()
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}