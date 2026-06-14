package com.example.splitbuddy.ui.profile

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
import com.example.splitbuddy.R
import com.example.splitbuddy.ui.components.InitialsAvatar
import com.example.splitbuddy.ui.components.LoadingView
import com.example.splitbuddy.ui.theme.Primary
import com.example.splitbuddy.ui.theme.gradient2
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    userId: String
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

        Spacer(modifier = Modifier.height(24.dp))

        // ── Avatar ────────────────────────────────────────────────────────────
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

        // ── Name ──────────────────────────────────────────────────────────────
        Text(
            text = "${state.value.firstName} ${state.value.lastName}".trim(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        if (state.value.userName.isNotBlank()) {
            Text(
                text = "@${state.value.userName}",
                style = MaterialTheme.typography.bodyMedium,
                color = Primary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ── Info cards ────────────────────────────────────────────────────────
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