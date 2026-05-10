package com.example.splitbuddy.domain.usecase.group

import com.example.splitbuddy.data.local.model.Trip
import com.example.splitbuddy.domain.repository.GroupRepository

class GetAllGroupsUseCase (
    private val repository: GroupRepository
) {
    suspend operator fun invoke(userId: String): List<Trip?> {
        return repository.getAllGroups(userId)
    }
}