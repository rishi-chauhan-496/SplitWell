package com.app.splitwell.domain.usecase.group

import com.app.splitwell.data.util.Resource
import com.app.splitwell.domain.repository.GroupRepository

class DeleteGroupUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(groupId: String): Resource<Boolean> =
        repository.deleteGroup(groupId)
}