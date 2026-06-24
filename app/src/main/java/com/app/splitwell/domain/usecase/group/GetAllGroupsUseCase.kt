package com.app.splitwell.domain.usecase.group

import com.app.splitwell.data.local.model.Trip
import com.app.splitwell.data.util.Resource
import com.app.splitwell.domain.repository.GroupRepository
import kotlinx.coroutines.flow.StateFlow

class GetAllGroupsUseCase (
    private val repository: GroupRepository
) {
    // Now exposes Flow instead of one-shot
    fun observe(): StateFlow<Resource<List<Trip>>> = repository.groupsFlow
    suspend fun sync(userId: String) = repository.sync(userId)
}