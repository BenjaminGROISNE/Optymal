package com.utbm.optymal.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.navigation.NavHostController
import com.utbm.optymal.FireStoreManager
import com.utbm.optymal.Screen
import com.utbm.optymal.SharedViewModels
import com.utbm.optymal.randomizeCar
import com.utbm.optymal.randomizeUser

@Composable
fun HomeScreen(nav :NavHostController,
               vms:SharedViewModels) {

    val loginViewModel=vms.login
     var dbManager by remember { mutableStateOf(FireStoreManager())}
     LaunchedEffect(Unit) {
       //  dbManager.getUser()
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
            onClick = { nav.navigate(Screen.Profile.route)  },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(text = "Go to Profile")
        }

        // Button 2: Go to Settings
        Button(
            onClick = { loginViewModel.signOut()
                nav.navigate(Screen.Login.route)},
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
        DeleteAccountButton(nav,vms)
        RandomDataDisplayScreenLazy(dbManager)
    }
}



@Composable
fun RandomDataDisplayScreenLazy(
    // If ShowUser/ShowCar are instance methods, you might pass dbManager here
     dbManager: FireStoreManager
) {
    // State variables remain the same
    var user1 by remember { mutableStateOf(randomizeUser()) }
    var car1 by remember { mutableStateOf(randomizeCar()) }
    var car2 by remember { mutableStateOf(randomizeCar()) }

    // Use LazyColumn instead of Column
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        // Add padding around the entire list content
        contentPadding = PaddingValues(16.dp),
        // Add vertical spacing between items automatically
        verticalArrangement = Arrangement.spacedBy(16.dp),
        // Center items horizontally (like the button if it doesn't fill width)
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Each distinct piece of UI becomes an item in the LazyColumn

        // Item for the Button
        item {
            Button(
                onClick = {
                    user1 = randomizeUser()
                    car1 = randomizeCar()
                    car2 = randomizeCar()
                }
                // Optional: make button wider if desired
                // modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Randomize Data")
            }
        }

        // Item for the "User Details" Header
        item {
            Text(
                text = "User Details",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth() // Ensure header takes full width
            )
        }

        // Item for the User Card
        item {
            // Assuming ShowUser takes care of its own padding and width
            dbManager.ShowUser(user = user1)
            // Or: dbManager.ShowUser(user = user1)
        }

        // Item for the "Car Details" Header
        item {
            Text(
                text = "Car Details",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth() // Ensure header takes full width
            )
        }

        // Item for the first Car Card
        item {
            // Assuming ShowCar takes care of its own padding and width
            dbManager.ShowCar(car = car1)
            // Or: dbManager.ShowCar(car = car1)
        }

        // Item for the second Car Card
        item {
            // Assuming ShowCar takes care of its own padding and width
            dbManager.ShowCar(car = car2)
            // Or: dbManager.ShowCar(car = car2)
        }

        // If you had a LIST of cars, you'd use items() like this:
        // items(listOfCars) { car ->
        //     ShowCar(car = car)
        // }
    }
}

@Composable
fun DeleteAccountButton(nav: NavHostController,vms:SharedViewModels) {
    val loginViewModel=vms.login
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