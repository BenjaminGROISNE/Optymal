package com.utbm.optymal.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import androidx.lifecycle.application
import com.google.firebase.auth.*
import com.utbm.optymal.R

enum class LoginType {GOOGLE, MAIL,PHONE}

class LoginScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val credentialManager: CredentialManager =
        CredentialManager.create(application.applicationContext)
    var auth: FirebaseAuth = Firebase.auth
    lateinit var loginChoice: LoginType
    var currentUser: FirebaseUser? = null
    var authenticated = mutableStateOf(false)

    var mail = mutableStateOf("")
    var password = mutableStateOf("")

    init {
        auth.addAuthStateListener { firebaseAuth ->
            currentUser = firebaseAuth.currentUser
        }
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
                    authenticated.value = true
                } else {
                    Log.e(TAG, "Failed to reload user.", task.exception)
                }
            }
        } else {
            authenticated.value = false
            // No user is signed in
        }
    }
    // [END on_start_check_user]

    private fun checkUserProvider(currentUser: FirebaseUser) {
        for (profile in currentUser.providerData) {
            when (profile.providerId) {
                EmailAuthProvider.PROVIDER_ID -> {
                    loginChoice = LoginType.MAIL
                    if (!currentUser.isEmailVerified) {
                        println("Email not verified")
                        sendEmailVerification()
                    }
                }

                GoogleAuthProvider.PROVIDER_ID -> {
                    loginChoice = LoginType.GOOGLE
                    println("Signed in with Google")
                }

                PhoneAuthProvider.PROVIDER_ID -> {
                    loginChoice = LoginType.PHONE
                    println("Signed in with Phone")
                }
            }
        }
    }


    public fun createAccount(email: String, password: String) {
        // [START create_user_with_email]

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener() { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "createUserWithEmail:success")
                currentUser = auth.currentUser
                authenticated.value = true
            } else {
                // If sign in fails, display a message to the user.
                // If sign in fails, handle the errors
                val exception = task.exception
                when (exception) {
                    is FirebaseAuthUserCollisionException -> {
                        // This error means the email is already in use
                        Log.w(TAG, "createUserWithEmail:failure - Email already in use")
                        Toast.makeText(
                                application.applicationContext,
                            "This email is already registered. Please use a different email.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    is FirebaseAuthWeakPasswordException -> {
                        // This error means the password is too weak
                        Log.w(TAG, "createUserWithEmail:failure - Weak password")
                        Toast.makeText(
                            application.applicationContext,
                            "Password is too weak. Please choose a stronger password.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    is FirebaseAuthInvalidCredentialsException -> {
                        // This error means the email format is invalid
                        Log.w(TAG, "createUserWithEmail:failure - Invalid email format")
                        Toast.makeText(
                            application.applicationContext,
                            "Invalid email format. Please check your email address.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    is FirebaseAuthEmailException -> {
                        // This error means the email format is invalid (generic)
                        Log.w(TAG, "createUserWithEmail:failure - Invalid email format")
                        Toast.makeText(
                            application.applicationContext,
                            "The email you entered is invalid. Please enter a valid email address.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    else -> {
                        // General errors (e.g., network issues)
                        Log.w(TAG, "createUserWithEmail:failure", exception)
                        Toast.makeText(
                            application.applicationContext,
                            "An unexpected error occurred. Please try again later.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            // [END create_user_with_email]
        }
    }

    private fun signIn(email: String, password: String, choice: LoginType) {
        // [START sign_in_with_email]
        when (choice) {
            LoginType.MAIL -> signInByMail(email, password)
            LoginType.GOOGLE -> signInByGoogle()
            LoginType.PHONE -> TODO()
        }


        // [END sign_in_with_email]
    }


    public fun signInByGoogle() {
        viewModelScope.launch {
            try {
                // Instantiate a Google sign-in request
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setServerClientId(application.applicationContext.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(true)
                    .build()

                // Create the Credential Manager request
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                // Call getCredential() inside coroutine
                val response =
                    credentialManager.getCredential(application.applicationContext, request)

                // Now handle the sign-in
                handleSignIn(response.credential)
            } catch (e: Exception) {
                Log.e(TAG, "Google Sign-In failed: ${e.localizedMessage}")
            }
        }
    }

    private fun handleSignIn(credential: Credential) {
        // Check if credential is of type Google ID
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            // Create Google ID Token
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

            // Sign in to Firebase with using the token
            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        } else {
            Log.w(TAG, "Credential is not of type Google ID!")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                } else {
                    // If sign in fails, display a message to the user
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    public fun signInByMail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    this.loginChoice = LoginType.MAIL
                    authenticated.value = true
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
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

    public fun signOut() {
        // Firebase sign out
        auth.signOut()
        authenticated.value = false
        // When a user signs out, clear the current user credential state from all credential providers.
        viewModelScope.launch {
            try {
                val clearRequest = ClearCredentialStateRequest()
                credentialManager.clearCredentialState(clearRequest)
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
            } else {

            }
        }
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}


