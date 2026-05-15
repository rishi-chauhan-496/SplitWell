package com.example.splitbuddy.ui.home_screen.friend

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splitbuddy.ui.components.EmptyStateView
import com.example.splitbuddy.ui.components.InitialsAvatar
import com.example.splitbuddy.ui.components.LoadingView
import com.example.splitbuddy.ui.theme.Primary
import org.koin.androidx.compose.koinViewModel

@Composable
fun FriendListScreen(ownerID: String) {

    val viewModel: FriendListViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.load(ownerID)
    }

    when {
        state.isLoading -> LoadingView()

        state.friends.isEmpty() -> EmptyStateView(
            message = "No friends yet\nJoin a group to see members here"
        )

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                items(state.friends) { friend ->
                    FriendCard(friend = friend)
                }
            }
        }
    }
}

@Composable
private fun FriendCard(friend: FriendItem) {
    val isDark = isSystemInDarkTheme()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .shadow(
                elevation    = 8.dp,
                shape        = RoundedCornerShape(16.dp),
                ambientColor = if (isDark) Color.White.copy(0.05f) else Primary.copy(0.1f),
                spotColor    = if (isDark) Color.White.copy(0.05f) else Primary.copy(0.15f)
            ),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar — reuses your existing InitialsAvatar component
            InitialsAvatar(
                name = friend.displayName,
                size = 46.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Username
                Text(
                    text       = "@${friend.userName}",
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Email
                Text(
                    text     = friend.email,
                    fontSize = 13.sp,
                    color    = MaterialTheme.colorScheme.surfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}