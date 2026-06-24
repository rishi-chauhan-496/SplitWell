package com.app.splitwell.domain.usecase.group

import com.app.splitwell.data.local.model.TripManager
import com.app.splitwell.domain.repository.GroupRepository


class GetGroupMembersUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(groupId: String): List<TripManager> {
        return repository.getGroupMemberByGroupId(groupId)
    }
}