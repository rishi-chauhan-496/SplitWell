package com.app.splitwell.domain.usecase.settlement

import com.app.splitwell.data.local.model.Settlement
import com.app.splitwell.data.local.query.SettlementQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetGroupSettlementsUseCase(
    private val settlementQuery: SettlementQuery
) {
    suspend operator fun invoke(groupId: String): List<Settlement> =
        withContext(Dispatchers.IO) {
            settlementQuery
                .getSettlementByTrip(groupId)
                .filter { !it.isDeleted }
        }
}