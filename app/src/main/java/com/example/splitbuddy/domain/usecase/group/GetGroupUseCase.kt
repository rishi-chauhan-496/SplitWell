package com.example.splitbuddy.domain.usecase.group

import com.example.splitbuddy.data.local.model.Trip
import com.example.splitbuddy.domain.repository.GroupRepository

class GetGroupUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(groupId: String): Trip? {
        return repository.getGroup(groupId)
    }
}