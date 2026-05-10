package com.example.splitbuddy.ui.intro_screen

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.splitbuddy.R
import com.example.splitbuddy.ui.home_screen.HomeActivity
import com.example.splitbuddy.ui.login_screen.LoginActivity
import com.example.splitbuddy.ui.theme.SplitBuddyTheme
import com.google.firebase.auth.auth
import kotlin.jvm.java

class IntroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("SplitBuddyPrefs", MODE_PRIVATE)
        val savedUserId = prefs.getString("userId", null)
        val auth = com.google.firebase.Firebase.auth

        if (auth.currentUser != null && !savedUserId.isNullOrBlank()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        setContent {
            SplitBuddyTheme {
                OnboardingScreen()
            }
        }
        hideSystemBars()
    }
    private fun hideSystemBars() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        // Hide only the navigation bar (bottom buttons)
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen() {

    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 3 })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->

            when (page) {

                0 -> OnboardingPage(
                    image = R.drawable.intro_1,
                    title = stringResource(R.string.onboard_title_1),
                    desc = stringResource(R.string.onboard_desc_1)
                )

                1 -> OnboardingPage(
                    image = R.drawable.intro_2,
                    title = stringResource(R.string.onboard_title_2),
                    desc = stringResource(R.string.onboard_desc_2)
                )

                2 -> OnboardingPage(
                    image = R.drawable.intro_3,
                    title = stringResource(R.string.onboard_title_3),
                    desc = stringResource(R.string.onboard_desc_3)
                )
            }
        }

        PagerIndicator(pagerState)

        Button(
            onClick = {
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Get Started")
        }
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SplitBuddyTheme {
        OnboardingScreen()
    }
}