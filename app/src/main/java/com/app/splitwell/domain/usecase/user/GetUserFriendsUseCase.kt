package com.app.splitwell.domain.usecase.user

import com.app.splitwell.data.remote.user.UserApiInterface
import com.app.splitwell.domain.model.Friend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetUserFriendsUseCase(
    private val userApiInterface: UserApiInterface
) {
    suspend operator fun invoke(userId: String): List<Friend> =
        withContext(Dispatchers.IO) {
            userApiInterface
                .getUserFriends(userId)
                .map { friend ->
                    Friend(
                        id          = friend.id,
                        userName    = friend.username,
                        email       = friend.email,
                        displayName = friend.firstName.ifBlank { friend.username }
                    )
                }
        }
}