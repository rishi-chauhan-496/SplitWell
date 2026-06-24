package com.app.splitwell.ui.login_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.app.splitwell.ui.theme.Primary
import com.app.splitwell.ui.theme.gradient
import com.app.splitwell.R

@Composable
fun LoginScreen(
    state: LoginUiState,
    onGoogleSignIn: () -> Unit
) {
    val context = LocalContext.current
    val networkErrorMsg = stringResource(R.string.error_check_network)

    LaunchedEffect(state.error) {
        if (state.error != null) {
            Toast.makeText(context, networkErrorMsg, Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── App logo / icon ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        brush = gradient,
                        shape = RoundedCornerShape(28.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.login_logo_initials),
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.login_tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.surfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ── Google Sign In button ─────────────────────────────────────────
            Button(
                onClick = onGoogleSignIn,
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.secondary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Primary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.google_icon),
                            contentDescription = "Google",
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.login_google_button),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.login_terms),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.surfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}