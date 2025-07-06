package com.example.boofilms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AuthNavigation(authManager: AuthManager) {
    val navController = rememberNavController()
    val isLoggedIn by remember { mutableStateOf(authManager.isLoggedIn()) }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "main" else "login"
    ) {
        composable("login") {
            LoginScreen(
                authManager = authManager,
                onLoginSuccess = { navController.navigate("main") },
                onRegisterClick = { navController.navigate("registration") },
                onLoginClick = { username, password ->
                    if (authManager.login(username, password)) {
                        navController.navigate("main")
                    }
                }
            )
        }
        composable("registration") {
            RegistrationScreen(
                authManager = authManager,
                onRegisterSuccess = { navController.navigate("main") },
                onBackClick = { navController.popBackStack() },
                onLoginClick = { navController.navigate("login") }
            )
        }
        composable("main") {
            MainScreen(
                authManager = authManager,
                onNavigateToCatalog = { navController.navigate("catalog") },
                onNavigateToAccount = { navController.navigate("account") },
                onNavigateToSettings = { navController.navigate("settings") }, // Добавьте эту строку
                onLogout = {
                    authManager.logout()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
        composable("catalog") {
            FilmCatalogScreen(
                onAccountClick = { navController.navigate("account") },
                onBackClick = { navController.popBackStack() }
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
