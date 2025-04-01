package com.utbm.optymal.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.utbm.optymal.FireStoreManager
import com.utbm.optymal.Screen
import com.utbm.optymal.viewModel.HomeScreenViewModel
import com.utbm.optymal.viewModel.LoginScreenViewModel

@Composable
fun HomeScreen(nav :NavHostController,
               viewModel: HomeScreenViewModel = viewModel(),
               loginViewModel:LoginScreenViewModel =viewModel()) {


     var dbManager by remember { mutableStateOf(FireStoreManager())}
     LaunchedEffect(Unit) {
         dbManager.setData()
     }
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
                        nav.navigate(Screen.Login.route)
                      },

            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Log Out")
        }
        // Button 4: Log Out
        DeleteAccountButton(nav,loginViewModel)

    }
}
@Composable
fun DeleteAccountButton(nav: NavHostController, loginViewModel: LoginScreenViewModel) {
    // State to control the visibility of the dialog
    var showDialog by remember { mutableStateOf(false) }

    // State to hold the entered password
    var password by remember { mutableStateOf(TextFieldValue("")) }

    // State to manage dialog input validation (e.g., whether password is provided)
    var passwordError by remember { mutableStateOf(false) }

    // Button to trigger the dialog
    Button(
        onClick = {
            showDialog = true // Show the dialog when the button is clicked
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Delete Account")
    }

    // Dialog that appears when the button is clicked
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },  // Dismiss the dialog when clicking outside
            title = { Text("Confirm Deletion") },
            text = {
                Column {
                    Text("Enter your password to confirm account deletion.")
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        isError = passwordError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (passwordError) {
                        Text(
                            text = "Password is required!",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (password.text.isEmpty()) {
                            // Show error if password is not entered
                            passwordError = true
                        } else {
                            passwordError = false

                            // Call the deleteUserAccount() function from the ViewModel
                            loginViewModel.deleteUserAccount(password.text,
                                onSuccess = {
                                    // Successfully deleted account, navigate to the login screen
                                    nav.navigate(Screen.Login.route)  // Navigate to the login screen
                                    Log.d("DeleteAccount", "Account successfully deleted")

                                    showDialog = false  // Close the dialog
                                },
                                onFailure = { exception ->
                                    // Handle failure (e.g., show a toast or a message)
                                    Log.w("DeleteAccount", "Failed to delete account: ${exception.message}")
                                    showDialog = false  // Close the dialog
                                })
                        }
                    }
                ) {
                    Text("Yes, Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false // Close the dialog without doing anything
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}