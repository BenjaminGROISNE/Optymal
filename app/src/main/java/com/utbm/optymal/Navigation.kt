package com.utbm.optymal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.utbm.optymal.screens.HomeScreen
import com.utbm.optymal.screens.LoginScreen
import com.utbm.optymal.screens.ProfileScreen
import com.utbm.optymal.viewModel.HomeScreenViewModel
import com.utbm.optymal.viewModel.LoginScreenViewModel

// Define your navigation routes
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Profile : Screen("profile")
}

@Composable
fun rememberSharedViewModels(): SharedViewModels {
    val loginViewModel: LoginScreenViewModel = viewModel()
    val homeViewModel: HomeScreenViewModel = viewModel()
    return remember { SharedViewModels(loginViewModel, homeViewModel) }
}

data class SharedViewModels(
    val login: LoginScreenViewModel,
    val home: HomeScreenViewModel
)

@Composable
fun Navigation(navController: NavHostController = rememberNavController()) {
    val vms = rememberSharedViewModels()
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(navController,vms)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController, vms)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController,vms)
        }
    }
}
