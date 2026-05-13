package com.example.splitbuddy.domain.usecase.group

import com.example.splitbuddy.domain.repository.GroupRepository
import com.example.splitbuddy.data.util.Resource

class UpdateGroupMemberUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(groupId: String, userId: String): Resource<Boolean> {
        return repository.updateGroupMemberByGroupId(groupId, userId)
    }
}