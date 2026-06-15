package com.example.splitbuddy.ui.invite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splitbuddy.ui.components.LoadingView
import com.example.splitbuddy.ui.theme.Primary
import com.example.splitbuddy.ui.theme.gradient
import org.koin.androidx.compose.koinViewModel

@Composable
fun InvitePreviewScreen(
    token: String,
    ownerID: String,
    onJoinGroup: (groupId: String) -> Unit,
    onCancel: () -> Unit
) {
    val viewModel: InvitePreviewViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(token) { viewModel.load(token) }

    // Navigate to GroupScreen when join succeeds
    LaunchedEffect(state.joinedGroupId) {
        state.joinedGroupId?.let { groupId ->
            onJoinGroup(groupId)
        }
    }

    when {
        state.isLoading -> LoadingView()

        state.error != null -> {
            // Invalid or expired link
            Box(
                modifier         = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier            = Modifier.padding(32.dp)
                ) {
                    Text(
                        text      = "❌",
                        fontSize  = 48.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text       = state.error ?: "Something went wrong",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign  = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedButton(onClick = onCancel) {
                        Text("Go Back")
                    }
                }
            }
        }

        else -> {
            Column(
                modifier            = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // ── Invite emoji header ───────────────────────────────────
                Text(text = "🎉", fontSize = 56.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text       = "You're Invited!",
                    style      = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(32.dp))

                // ── Group info card ───────────────────────────────────────
                Surface(
                    modifier        = Modifier.fillMaxWidth(),
                    shape           = RoundedCornerShape(20.dp),
                    color           = MaterialTheme.colorScheme.surface,
                    shadowElevation = 6.dp
                ) {
                    Column(
                        modifier            = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Group initial avatar
                        Box(
                            modifier         = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(gradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text      = state.groupName.firstOrNull()
                                    ?.uppercase() ?: "G",
                                color     = Color.White,
                                fontSize  = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text       = state.groupName,
                            style      = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign  = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text  = "${state.memberCount} members",
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text  = "Invited by ${state.createdByName}",
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        // Show expiry only if provided
                        state.expiresAt?.let { expiry ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text     = "Expires: $expiry",
                                color    = if (state.isExpired) Color(0xFFC62828)
                                else MaterialTheme.colorScheme.surfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // ── Join / Expired button ─────────────────────────────────
                if (state.isExpired) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(14.dp),
                        color    = MaterialTheme.colorScheme.surfaceVariant.copy(0.2f)
                    ) {
                        Text(
                            text      = "This invite link has expired",
                            modifier  = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            color     = Color(0xFFC62828),
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Button(
                        onClick  = { viewModel.acceptInvite(token, ownerID) },
                        enabled  = !state.isAccepting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape    = RoundedCornerShape(14.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = Primary
                        )
                    ) {
                        if (state.isAccepting) {
                            CircularProgressIndicator(
                                modifier    = Modifier.size(20.dp),
                                color       = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text     = "Join Group",
                                fontSize = 16.sp,
                                color    = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Cancel / already member
                TextButton(onClick = onCancel) {
                    Text(
                        text  = "Already a member? Go back",
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }
}