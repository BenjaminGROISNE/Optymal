package com.utbm.optymal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.utbm.optymal.screens.*
import com.utbm.optymal.viewModel.*

// Define your navigation routes
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
}

@Composable
fun Navigation(navController: NavHostController = rememberNavController()) {
    // Create the NavController
    // Set up your NavHost and the graph for the app
    val loginViewModel: LoginScreenViewModel = viewModel()
    val homeViewModel: HomeScreenViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        // Add your composable screens to the NavHost
        composable(Screen.Login.route) {
            LoginScreen(navController,loginViewModel)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController,homeViewModel,loginViewModel)
        }
    }
}