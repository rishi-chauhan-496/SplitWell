package com.example.splitbuddy.domain.usecase.group

import com.example.splitbuddy.data.remote.group.RemoveMembersRequest
import com.example.splitbuddy.data.util.Resource
import com.example.splitbuddy.domain.repository.GroupRepository

class RemoveMembersFromGroupUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(
        groupId: String,
        request: RemoveMembersRequest
    ): Resource<Boolean> = repository.removeMembersFromGroup(groupId, request)
}