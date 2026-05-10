package com.example.splitbuddy.domain.usecase.group

import com.example.splitbuddy.data.remote.group.UpdateGroupRequest
import com.example.splitbuddy.domain.repository.GroupRepository

class UpdateGroupUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(groupId: String, request: UpdateGroupRequest): Boolean {
        return repository.updateGroup(groupId, request)
    }
}