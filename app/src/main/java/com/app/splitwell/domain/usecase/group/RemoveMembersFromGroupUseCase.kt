package com.app.splitwell.domain.usecase.group

import com.app.splitwell.data.remote.group.RemoveMembersRequest
import com.app.splitwell.data.util.Resource
import com.app.splitwell.domain.repository.GroupRepository

class RemoveMembersFromGroupUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(
        groupId: String,
        request: RemoveMembersRequest
    ): Resource<Boolean> = repository.removeMembersFromGroup(groupId, request)
}