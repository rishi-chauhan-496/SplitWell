package com.example.splitbuddy.domain.usecase.group

import com.example.splitbuddy.data.local.model.Trip
import com.example.splitbuddy.data.remote.group.CreateGroupRequest
import com.example.splitbuddy.data.util.Resource
import com.example.splitbuddy.domain.repository.GroupRepository

class CreateGroupUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(request: CreateGroupRequest): Resource<Trip> =
        repository.groupCreation(request)
}