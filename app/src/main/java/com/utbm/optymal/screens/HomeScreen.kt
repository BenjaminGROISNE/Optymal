package com.utbm.optymal.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.utbm.optymal.viewModel.HomeScreenViewModel
import com.utbm.optymal.viewModel.LoginScreenViewModel

@Composable
fun HomeScreen(nav :NavHostController,
               viewModel: HomeScreenViewModel = viewModel(),
               loginViewModel:LoginScreenViewModel =viewModel()) {

//lol
   // var dbManager by remember { mutableStateOf(FireStoreManager())}
  //  LaunchedEffect(Unit) {
 //       dbManager.setData()
//    }
    // Main Column layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title Text
        Text(
            text = "Welcome to the Home Menu",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Button 1: Go to Profile
        Button(
            onClick = { /* Handle action */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(text = "Go to Profile")
        }

        // Button 2: Go to Settings
        Button(
            onClick = { /* Handle action */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(text = "Go to Settings")
        }

        // Button 3: Log Out
        Button(
            onClick = { loginViewModel.signOut()
                        nav.navigate("login")
                      },

            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Log Out")
        }

    }
}

