package com.app.splitwell.domain.usecase.user

import com.app.splitwell.data.local.model.User
import com.app.splitwell.data.local.query.UserQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetUserProfileUseCase(
    private val userQuery: UserQuery
) {
    suspend operator fun invoke(userId: String): User? =
        withContext(Dispatchers.IO) {
            userQuery.getUser(userId)
        }
}