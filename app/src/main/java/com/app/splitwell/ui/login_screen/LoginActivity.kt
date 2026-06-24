package com.app.splitwell.ui.login_screen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.app.splitwell.ui.home_screen.HomeActivity
import com.app.splitwell.ui.theme.SplitWellTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import android.content.SharedPreferences
import org.koin.android.ext.android.inject
import android.widget.Toast
import com.google.firebase.FirebaseNetworkException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.app.splitwell.R
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL

class LoginActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by viewModel()
    private val sharedPreferences: SharedPreferences by inject()
    private lateinit var credentialManager: CredentialManager
    private val auth by lazy { Firebase.auth }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ── Check if already logged in ────────────────────────────────────────
        val savedUserId = sharedPreferences.getString("userId", null)

        if (auth.currentUser != null && !savedUserId.isNullOrBlank()) {
            goToHome()
            return
        }

        credentialManager = CredentialManager.create(this)

        setContent {
            SplitWellTheme {
                val state = viewModel.state.collectAsState()

                // Navigate to HomeActivity when login succeeds
                LaunchedEffect(state.value.isLoggedIn) {
                    if (state.value.isLoggedIn) goToHome()
                }

                LoginScreen(
                    state = state.value,
                    onGoogleSignIn = { startGoogleSignIn() }
                )
            }
        }
    }

    // ── Google Sign In via CredentialManager ──────────────────────────────────

    private fun startGoogleSignIn() {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(getString(R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    context = this@LoginActivity,
                    request = request
                )
                handleCredential(result.credential)

            } catch (e: GetCredentialException) {
                Log.e("LoginActivity", "Google Sign-In failed", e)

                val isNetworkError = generateSequence(e as Throwable) { it.cause }
                    .any { it is UnknownHostException || it is ConnectException || it is SocketTimeoutException }

                if (isNetworkError) {
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.error_check_network),
                        Toast.LENGTH_LONG
                    ).show()
                }

                viewModel.clearError()
            }
        }
    }

    private fun handleCredential(credential: androidx.credentials.Credential) {
        if (credential is CustomCredential &&
            credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
            firebaseSignIn(googleCredential.idToken)
        }
    }

    private fun firebaseSignIn(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(firebaseCredential)
            .addOnSuccessListener { result ->
                val user = result.user ?: return@addOnSuccessListener
                // Pass result to ViewModel — it calls the backend
                viewModel.handleGoogleSignIn(
                    googleUid   = user.uid,
                    email       = user.email,
                    displayName = user.displayName
                )
            }
            .addOnFailureListener { e ->
                Log.e("LoginActivity", "Firebase sign in failed", e)

                if (e is FirebaseNetworkException) {
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.error_check_network),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    private fun goToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}