package com.utbm.optymal

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.rememberCompositionContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth


class LoginPageViewModel: ViewModel() {
   lateinit var auth: FirebaseAuth
    init{
        var auth = Firebase.auth
    }


    // [START on_start_check_user]
    public fun Start() {
        // Check if user is signed in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Refresh user data
            currentUser.reload().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (currentUser.isEmailVerified) {

                    } else {
                    }
                    updateUI(currentUser)
                } else {
                    Log.e(TAG, "Failed to reload user.", task.exception)
                    updateUI(null)
                }
            }
        } else {
            // No user is signed in
            updateUI(null)
        }
    }
    // [END on_start_check_user]

    private fun createAccount(email: String, password: String) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(){ task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    updateUI(null)
                }
            }
        // [END create_user_with_email]
    }

    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    updateUI(null)
                }
            }
        // [END sign_in_with_email]
    }

    private fun sendEmailVerification() {
        // [START send_email_verification]
        val user = auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
            } else {
                Log.e(TAG, "sendEmailVerification failed", task.exception)
            }
        }
        // [END send_email_verification]
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {

            // Redirect to the main activity or update UI accordingly
        } else {

            // Show login/signup buttons if needed
        }
    }

    private fun reload() {
        auth.currentUser?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user?.isEmailVerified == true) {

                } else {

                }
                updateUI(user)
            } else {

            }
        }
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}