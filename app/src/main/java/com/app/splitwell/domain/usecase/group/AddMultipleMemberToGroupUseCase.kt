package com.app.splitwell.domain.usecase.group

import com.app.splitwell.data.remote.group.AddMembersRequest
import com.app.splitwell.data.util.Resource
import com.app.splitwell.domain.repository.GroupRepository

class AddMultipleMemberToGroupUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(groupId: String, body: AddMembersRequest): Resource<Boolean> =
        repository.addMultipleMemberToGroup(groupId, body)
}