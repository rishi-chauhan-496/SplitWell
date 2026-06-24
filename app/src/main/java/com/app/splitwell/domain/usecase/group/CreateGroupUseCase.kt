package com.app.splitwell.domain.usecase.group

import com.app.splitwell.data.local.model.Trip
import com.app.splitwell.data.remote.group.CreateGroupRequest
import com.app.splitwell.data.util.Resource
import com.app.splitwell.domain.repository.GroupRepository

class CreateGroupUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(request: CreateGroupRequest): Resource<Trip> =
        repository.groupCreation(request)
}