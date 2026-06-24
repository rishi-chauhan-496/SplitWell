package com.app.splitwell.domain.usecase.group

import com.app.splitwell.data.local.model.Trip
import com.app.splitwell.domain.repository.GroupRepository

class GetGroupUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(groupId: String): Trip? {
        return repository.getGroup(groupId)
    }
}