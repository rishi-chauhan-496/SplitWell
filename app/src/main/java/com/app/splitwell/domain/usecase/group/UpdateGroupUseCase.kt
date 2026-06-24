package com.app.splitwell.domain.usecase.group

import com.app.splitwell.data.remote.group.UpdateGroupRequest
import com.app.splitwell.data.util.Resource
import com.app.splitwell.domain.repository.GroupRepository

class UpdateGroupUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(groupId: String, request: UpdateGroupRequest): Resource<Boolean> =
        repository.updateGroup(groupId, request)
}