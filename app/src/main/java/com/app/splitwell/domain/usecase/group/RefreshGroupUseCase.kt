package com.app.splitwell.domain.usecase.group

import com.app.splitwell.data.local.model.Trip
import com.app.splitwell.domain.repository.GroupRepository

/**
 * Fetches this one group fresh from the network and writes it (and its
 * members) into local storage. Unlike GetGroupUseCase, which only reads
 * whatever's already cached, this is what actually picks up a rename or
 * a membership change made by someone else.
 */
class RefreshGroupUseCase(
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke(groupId: String): Trip? =
        groupRepository.refreshGroup(groupId)
}