package com.utbm.optymal

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build.ID
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.rememberCompositionContext
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
enum class LoginType {GOOGLE, MAIL,PHONE}

class LoginPageViewModel(private val context: Context): ViewModel() {

    private val credentialManager: CredentialManager = CredentialManager.create(context)
   lateinit var auth: FirebaseAuth
   lateinit var loginChoice: LoginType
   var currentUser: FirebaseUser? = null
   var authentified=false
    init{
        var auth = Firebase.auth
        onStart()
    }


    // [START on_start_check_user]
    private fun onStart() {
        // Check if user is signed in
        currentUser = auth.currentUser
        if (currentUser != null) {
            // Refresh user data
            currentUser!!.reload().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    checkUserProvider(currentUser!!)
                }
                else {
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

    private fun checkUserProvider(currentUser: FirebaseUser){
        for (profile in currentUser.providerData) {
            when (profile.providerId) {
                EmailAuthProvider.PROVIDER_ID -> {
                    loginChoice=LoginType.MAIL
                    if (!currentUser.isEmailVerified) {
                        println("Email not verified")
                        sendEmailVerification()
                    }
                }
                GoogleAuthProvider.PROVIDER_ID -> {
                    loginChoice=LoginType.GOOGLE
                    println("Signed in with Google")
                }
                PhoneAuthProvider.PROVIDER_ID -> {
                    loginChoice=LoginType.PHONE
                    println("Signed in with Phone")
                }
            }
        }
    }


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

    private fun signIn(email: String, password: String,choice:LoginType) {
        // [START sign_in_with_email]
        when(choice){
            LoginType.MAIL ->signInByMail(email,password)
            LoginType.GOOGLE -> signInByGoogle()
            LoginType.PHONE -> TODO()
        }



        // [END sign_in_with_email]
    }

    private fun signInByGoogle(){
// Instantiate a Google sign-in request
        val googleIdOption = GetGoogleIdOption.Builder()
            // Your server's client ID, not your Android client ID.
            .setServerClientId(R.string.web_client_id.toString())
            // Only show accounts previously used to sign in.
            .setFilterByAuthorizedAccounts(true)
            .build()

// Create the Credential Manager request
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }
    private fun signInByMail(email: String,password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    this.loginChoice= LoginType.MAIL
                    updateUI(auth.currentUser)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    updateUI(null)
                }
            }
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
    private fun signOut() {
        // Firebase sign out
        auth.signOut()

        // When a user signs out, clear the current user credential state from all credential providers.
        viewModelScope.launch {
            try {
                val clearRequest = ClearCredentialStateRequest()
                credentialManager.clearCredentialState(clearRequest)
                updateUI(null)
            } catch (e: ClearCredentialException) {
                Log.e(TAG, "Couldn't clear user credentials: ${e.localizedMessage}")
            }
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


class LoginPageViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginPageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginPageViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}