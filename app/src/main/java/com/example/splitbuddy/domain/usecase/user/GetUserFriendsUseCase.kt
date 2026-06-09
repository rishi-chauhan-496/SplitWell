package com.example.splitbuddy.domain.usecase.user

import com.example.splitbuddy.data.remote.user.UserApiInterface
import com.example.splitbuddy.ui.home_screen.friend.FriendItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetUserFriendsUseCase(
    private val userApiInterface: UserApiInterface
) {
    suspend operator fun invoke(userId: String): List<FriendItem> =
        withContext(Dispatchers.IO) {
            userApiInterface
                .getUserFriends(userId)
                .map { friend ->
                    FriendItem(
                        id          = friend.id,
                        userName    = friend.username,
                        email       = friend.email,
                        displayName = friend.firstName.ifBlank { friend.username }
                    )
                }
        }
}