package com.example.splitbuddy.domain.usecase.group

import com.example.splitbuddy.data.local.model.TripManager
import com.example.splitbuddy.domain.repository.GroupRepository


class GetGroupMembersUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(groupId: String): List<TripManager> {
        return repository.getGroupMemberByGroupId(groupId)
    }
}