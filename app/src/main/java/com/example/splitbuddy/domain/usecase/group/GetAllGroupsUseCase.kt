package com.example.splitbuddy.domain.usecase.group

import com.example.splitbuddy.data.local.model.Trip
import com.example.splitbuddy.data.util.Resource
import com.example.splitbuddy.domain.repository.GroupRepository
import kotlinx.coroutines.flow.StateFlow

class GetAllGroupsUseCase (
    private val repository: GroupRepository
) {
    // Now exposes Flow instead of one-shot
    fun observe(): StateFlow<Resource<List<Trip>>> = repository.groupsFlow
    suspend fun sync(userId: String) = repository.sync(userId)
}