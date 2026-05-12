package com.example.splitbuddy.domain.usecase.group

import com.example.splitbuddy.data.util.Resource
import com.example.splitbuddy.domain.repository.GroupRepository

class DeleteGroupUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(groupId: String): Resource<Boolean> =
        repository.deleteGroup(groupId)
}