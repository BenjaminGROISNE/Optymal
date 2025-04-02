package com.utbm.optymal.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.utbm.optymal.Screen
import com.utbm.optymal.viewModel.LoginScreenViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    loginViewModel: LoginScreenViewModel
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(current = "profile") { destination ->
                navController.navigate(destination) {
                    popUpTo(Screen.Home.route) { inclusive = false }
                    launchSingleTop = true
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(200.dp))

            Text(
                text = "Optymal",
                style = MaterialTheme.typography.headlineLarge,
                color = Color(0xFFD60080),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            ProfileItem(text = "Modifier mon profil") { /* TODO */ }

            ProfileItem(text = "Informations Pratiques") { /* TODO */ }

            ProfileItem(text = "Appeler le service client") { /* TODO */ }

            Spacer(modifier = Modifier.height(16.dp))

            ProfileItem(
                text = "Se déconnecter",
                textColor = Color(0xFFD81B60)
            ) {
                loginViewModel.signOut()
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(current: String, onNavigate: (String) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = current == "home",
            onClick = { onNavigate(Screen.Home.route) },
            icon = { Text("🏠") },
            label = { Text("Accueil") }
        )
        NavigationBarItem(
            selected = current == "profile",
            onClick = { onNavigate(Screen.Profile.route) },
            icon = { Text("👤") },
            label = { Text("Profil") }
        )
    }
}

@Composable
fun ProfileItem(
    text: String,
    textColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Text(
        text = text,
        fontSize = 16.sp,
        color = textColor,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { onClick() }
    )
}
