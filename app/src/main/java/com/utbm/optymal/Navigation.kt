package com.utbm.optymal

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.utbm.optymal.screens.LoginScreen

// Define your navigation routes
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
}

@Composable
fun Navigation(navController: NavHostController) {
    // Set up your NavHost and the graph for the app
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        // Add your composable screens to the NavHost
        composable(Screen.Login.route) {
            LoginScreen()
        }
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
    }
}