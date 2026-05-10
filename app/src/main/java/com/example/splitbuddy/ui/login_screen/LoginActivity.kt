package com.example.splitbuddy.ui.login_screen

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
import com.example.splitbuddy.ui.home_screen.HomeActivity
import com.example.splitbuddy.ui.theme.SplitBuddyTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.splitbuddy.R
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL

class LoginActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by viewModel()
    private lateinit var credentialManager: CredentialManager
    private val auth by lazy { Firebase.auth }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ── Check if already logged in ────────────────────────────────────────
        val prefs = getSharedPreferences("SplitBuddyPrefs", MODE_PRIVATE)
        val savedUserId = prefs.getString("userId", null)

        if (auth.currentUser != null && !savedUserId.isNullOrBlank()) {
            goToHome()
            return
        }

        credentialManager = CredentialManager.create(this)

        setContent {
            SplitBuddyTheme {
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
            }
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    private fun goToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}