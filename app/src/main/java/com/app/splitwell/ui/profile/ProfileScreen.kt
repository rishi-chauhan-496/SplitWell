package com.app.splitwell.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.splitwell.ui.components.InitialsAvatar
import com.app.splitwell.ui.components.LoadingView
import com.app.splitwell.ui.theme.Primary
import com.app.splitwell.ui.theme.gradient2
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import org.koin.androidx.compose.koinViewModel
import com.app.splitwell.R

@Composable
fun ProfileScreen(
    userId: String,
    onSignOut: () -> Unit
) {
    val viewModel: ProfileEditViewModel = koinViewModel()
    val state = viewModel.state.collectAsState()

    LaunchedEffect(userId) {
        viewModel.load(userId)
    }

    if (state.value.isLoading) {
        LoadingView()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ── Scrollable content ───────────────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            // ── Avatar ────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(brush = gradient2, shape = RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                InitialsAvatar(
                    name = state.value.firstName.ifBlank { "?" },
                    size = 100.dp,
                    fontSize = 40.sp,
                    backgroundColor = Color.Transparent,
                    textColor = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Name ──────────────────────────────────────────────────────────
            Text(
                text = "${state.value.firstName} ${state.value.lastName}".trim(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )

            if (state.value.userName.isNotBlank()) {
                Text(
                    text = stringResource(R.string.username_prefix, state.value.userName),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Primary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Info cards ────────────────────────────────────────────────────
            ProfileInfoCard(
                label = stringResource(R.string.profile_label_email),
                value = state.value.email.ifBlank { "Not set" }
            )

            Spacer(modifier = Modifier.height(8.dp))

            ProfileInfoCard(
                label = stringResource(R.string.profile_label_phone),
                value = state.value.contact.ifBlank { "Not set" }
            )

            Spacer(modifier = Modifier.height(8.dp))

            ProfileInfoCard(
                label = stringResource(R.string.profile_label_username),
                value = state.value.userName.ifBlank { stringResource(R.string.not_set) }
            )
        }

        // ── Sign out — anchored to the bottom, outside the scrollable area ──
        var showSignOutConfirm by remember { mutableStateOf(false) }

        OutlinedButton(
            onClick = { showSignOutConfirm = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(text = stringResource(R.string.profile_sign_out))
        }

        if (showSignOutConfirm) {
            AlertDialog(
                onDismissRequest = { showSignOutConfirm = false },
                title = { Text(stringResource(R.string.profile_sign_out_confirm_title)) },
                text = { Text(stringResource(R.string.profile_sign_out_confirm_message)) },
                confirmButton = {
                    TextButton(onClick = {
                        showSignOutConfirm = false
                        onSignOut()
                    }) {
                        Text(stringResource(R.string.profile_sign_out))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSignOutConfirm = false }) {
                        Text(stringResource(R.string.profile_sign_out_cancel))
                    }
                }
            )
        }
    }
}

// ── Info card composable ──────────────────────────────────────────────────────
@Composable
private fun ProfileInfoCard(
    label: String,
    value: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}