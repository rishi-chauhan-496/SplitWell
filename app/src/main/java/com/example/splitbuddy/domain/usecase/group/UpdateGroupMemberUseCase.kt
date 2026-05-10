package com.example.splitbuddy.domain.usecase.group

import com.example.splitbuddy.domain.repository.GroupRepository

class UpdateGroupMemberUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(groupId: String, userId: String): Boolean {
        return repository.updateGroupMemberByGroupId(groupId, userId)
    }
}