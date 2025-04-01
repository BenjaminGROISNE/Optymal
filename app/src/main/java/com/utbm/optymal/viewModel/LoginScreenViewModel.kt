package com.utbm.optymal.viewModel

import android.app.Activity
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.utbm.optymal.FireStoreManager
import com.utbm.optymal.R
import kotlinx.coroutines.launch


class LoginScreenViewModel(application: Application) : AndroidViewModel(application) {


    val dbManager=FireStoreManager()

    private val credentialManager: CredentialManager = CredentialManager.create(application.applicationContext)
    var auth: FirebaseAuth = Firebase.auth
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

    private fun onStart() {
        // Check if user is signed in
        currentUser = auth.currentUser
        if (currentUser != null) {
            // Refresh user data
            reload()
        } else authenticated.value = false
    }


    fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener() { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "createUserWithEmail:success")
                currentUser = auth.currentUser
                authenticated.value = true
                dbManager.addUser(currentUser!!.uid,email)
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
        }
    }

    fun deleteUserAccount(password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val currentUser = auth.currentUser

        // Check if the user is logged in
        if (currentUser != null) {
            val credential = EmailAuthProvider.getCredential(currentUser.email!!, password)

            // Re-authenticate the user
            currentUser.reauthenticate(credential)
                .addOnCompleteListener { reAuthTask ->
                    if (reAuthTask.isSuccessful) {
                        // Proceed with account deletion
                        dbManager.deleteUser(currentUser.uid)
                        currentUser.delete()
                            .addOnCompleteListener { deleteTask ->
                                if (deleteTask.isSuccessful) {
                                    Log.d("Firebase", "User account deleted")
                                    authenticated.value=false
                                    onSuccess()
                                } else {
                                    onFailure(deleteTask.exception ?: Exception("Unknown error"))
                                }
                            }
                    } else {
                        onFailure(reAuthTask.exception ?: Exception("Re-authentication failed"))
                    }
                }
        } else {
            onFailure(Exception("No user logged in"))
        }
    }

    fun signInByGoogle(activity: Activity?) {
        val currentActivity = activity ?: run {
            Log.e(TAG, "Activity is null, cannot proceed with Google Sign-In.")
            // Update UI State to show error
            // _signInState.value = SignInState.Error("An unexpected error occurred.")
            return
        }

        viewModelScope.launch {
            // _signInState.value = SignInState.Loading // Update UI State
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setServerClientId(application.applicationContext.getString(R.string.default_web_client_id))
                    // Consider setting autoSelect to false initially for better debugging
                    .setAutoSelectEnabled(true)
                    // setFilterByAuthorizedAccounts(false) is usually better if you want
                    // the user to pick from *any* Google account on the device, not just
                    // ones previously used with your app's client ID.
                    .setFilterByAuthorizedAccounts(true)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                Log.d(TAG, "Requesting Google credential...")
                val response = credentialManager.getCredential(currentActivity, request)
                Log.d(TAG, "Google credential received successfully.")
                handleSignIn(response.credential) // Handles success

            } catch (e: NoCredentialException) { // Catch specific exception if available/applicable
                Log.w(TAG, "Google Sign-In failed: No credential found.", e)
                Toast.makeText(
                    application.applicationContext,
                    "No Google Account found on this device. Please add one in device Settings or use another sign-in method.",
                    Toast.LENGTH_LONG
                ).show()
                // INFORM USER AND OFFER ALTERNATIVES
                // _signInState.value = SignInState.Error(
                //    "No Google Account found on this device. Please add one in device Settings or use another sign-in method."
                // )
                // Trigger UI to show other options (Email/Password, etc.)

            } catch (e: GetCredentialException) { // Catch broader credential exceptions
                Log.e(TAG, "Google Sign-In failed via GetCredentialException", e)
                Toast.makeText(
                    application.applicationContext,
                    "An error occurred during Google Sign-In. Please try again later.",
                    Toast.LENGTH_LONG
                ).show()
                // Handle other credential errors (e.g., user cancellation, interruption)
                // You might check e.type for more details if the API provides useful types
                // _signInState.value = SignInState.Error("Google Sign-In failed: ${e.message}")
                // Offer alternatives

            } catch (e: Exception) { // Catch any other unexpected exceptions
                Log.e(TAG, "Google Sign-In failed with generic exception", e)
                Toast.makeText(
                    application.applicationContext,
                    "An error occurred during Google Sign-In. Please try again later.",
                    Toast.LENGTH_LONG
                ).show()
                // _signInState.value = SignInState.Error("An error occurred during Google Sign-In.")
                // Offer alternatives
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
                    dbManager.addUser(currentUser!!.uid,currentUser!!.email!!)
                    // Sign in success, update UI with the signed-in user's information
                    authenticated.value=true
                    Log.d(TAG, "signInWithCredential:success")
                } else {
                    // If sign in fails, display a message to the user
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    fun signInByMail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    authenticated.value = true
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                }
            }
    }

    fun signOut() {
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
        currentUser?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                authenticated.value = true
            } else {
                Log.e(TAG, "Failed to reload user.", task.exception)
            }
        }
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}


