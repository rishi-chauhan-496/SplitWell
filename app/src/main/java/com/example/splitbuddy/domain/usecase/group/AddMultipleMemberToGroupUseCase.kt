package com.example.splitbuddy.domain.usecase.group

import com.example.splitbuddy.data.remote.group.AddMembersRequest
import com.example.splitbuddy.domain.repository.GroupRepository

class AddMultipleMemberToGroupUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(groupId: String, body: AddMembersRequest): Boolean {
        return repository.addMultipleMemberToGroup(groupId, body)
    }
}