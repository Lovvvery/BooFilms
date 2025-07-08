package com.example.boofilms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.boofilms.viewmodel.MovieViewModel

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
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate("registration") },
                onGuestLogin = {
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
            val viewModel: MovieViewModel = viewModel() // или hiltViewModel()
            val movies by viewModel.movies.collectAsState()
            val isLoading by viewModel.loading.collectAsState()

            FilmCatalogScreen(
                onMenuClick = {  },
                onAccountClick = { navController.navigate("account") },
                onBackClick = { navController.popBackStack() },
                movies = movies,
                isLoading = isLoading,
                onMovieClick = { movie ->
                    navController.navigate("movieDetails/${movie.id}")
                }
            )
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