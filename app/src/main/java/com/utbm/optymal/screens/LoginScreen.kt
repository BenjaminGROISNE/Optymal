package com.utbm.optymal.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.utbm.optymal.viewModel.LoginScreenViewModel
import com.utbm.optymal.R

@Preview(showBackground = true)
@Composable
fun LoginScreen(nav :NavHostController?=null,viewModel: LoginScreenViewModel = viewModel()){

    Surface(modifier = Modifier.fillMaxSize().background(Color.White)){
        Column (verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
            var showPassword by remember { mutableStateOf(false) }
            TextField(
                value = viewModel.mail.value,
                onValueChange = { viewModel.mail.value = it },
                label = { Text("Mail") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = viewModel.password.value,
                onValueChange = { viewModel.password.value = it },
                label = { Text("Password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation =if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) ImageVector.vectorResource(R.drawable.eye_password_hide_svgrepo_com)
                            else ImageVector.vectorResource(R.drawable.eye_password_show_svgrepo_com),
                            contentDescription = if (showPassword) "Hide password" else "Show password"
                        )
                    }
                },
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        if (viewModel.mail.value.isNotEmpty() && viewModel.password.value.isNotEmpty()) {
                            viewModel.signInByMail(viewModel.mail.value, viewModel.password.value) // Call the sign-in function
                        } else {
                            // Handle the case when email or password is empty
                            Log.w("SignInScreen", "Email or password cannot be empty.")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(text = "Connect")
                }
                Button(
                    onClick = {
                        if (viewModel.mail.value.isNotEmpty() && viewModel.password.value.isNotEmpty()) {
                            viewModel.createAccount(viewModel.mail.value, viewModel.password.value) // Call the sign-in function
                        } else {
                            // Handle the case when email or password is empty
                            Log.w("SignInScreen", "Email or password cannot be empty.")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(text = "Create account")
                }
            }
            GoogleSignInButton(onClick = { viewModel.signInByGoogle() })
        }

    }

    when(viewModel.authenticated.value) {
        true -> nav?.navigate("home")
        false -> {}
    }

}

@Composable
fun GoogleSignInButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(50.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        elevation = ButtonDefaults.buttonElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_google_logo), // ✅ Correct for vector drawables (XML)
                contentDescription = "Google Sign-In",
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp)
            )
            Text(
                text = "Sign in with Google",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
