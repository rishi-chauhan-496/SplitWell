package com.example.splitbuddy.ui.home_screen.friend

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splitbuddy.R
import com.example.splitbuddy.ui.components.InitialsAvatar
import com.example.splitbuddy.ui.components.ScreenStateWrapper
import com.example.splitbuddy.ui.components.SplitBuddyCard
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendListScreen(ownerID: String) {

    val viewModel: FriendListViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.load(ownerID) }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = { viewModel.load(ownerID, isRefresh = true) }
    ) {
        ScreenStateWrapper(
            isLoading = state.isLoading,
            isEmpty = state.friends.isEmpty(),
            emptyMessage = stringResource(R.string.friends_empty_message)
        ) {
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
    SplitBuddyCard(elevation = 8.dp) {
        Row(
            modifier = Modifier
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
                    text = "@${friend.userName}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Email
                Text(
                    text = friend.email,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}