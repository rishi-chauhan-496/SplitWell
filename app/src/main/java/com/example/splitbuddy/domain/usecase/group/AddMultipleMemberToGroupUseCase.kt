package com.example.splitbuddy.domain.usecase.group

import com.example.splitbuddy.data.remote.group.AddMembersRequest
import com.example.splitbuddy.data.util.Resource
import com.example.splitbuddy.domain.repository.GroupRepository

class AddMultipleMemberToGroupUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(groupId: String, body: AddMembersRequest): Resource<Boolean> =
        repository.addMultipleMemberToGroup(groupId, body)
}