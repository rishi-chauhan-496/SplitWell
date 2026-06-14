package com.example.splitbuddy.domain.calculator

import com.example.splitbuddy.data.local.model.Expense
import com.example.splitbuddy.data.local.model.ExpenseShare
import com.example.splitbuddy.domain.model.SettlementSuggestion

class SettlementCalculator {

    // ── Step 1 ────────────────────────────────────────────────────────────────
    // Pure arithmetic — no DB, no API
    // Returns userId → net balance (positive = owed money, negative = owes money)
    fun calculateNetBalances(
        expenses: List<Expense>,
        sharesByExpense: Map<String, List<ExpenseShare>>
    ): Map<String, Double> {

        val netBalance = mutableMapOf<String, Double>()

        for (expense in expenses) {

            // Payer gets credited the full amount
            netBalance[expense.paidByUser] =
                (netBalance[expense.paidByUser] ?: 0.0) + expense.amount

            // Each included member gets debited their share
            val shares = sharesByExpense[expense.id] ?: emptyList()

            for (share in shares) {
                if (!share.isIncluded) continue   // skip — not part of this expense

                netBalance[share.userId] =
                    (netBalance[share.userId] ?: 0.0) - share.sharedAmount
            }
        }

        return netBalance
    }

    // ── Step 2 ────────────────────────────────────────────────────────────────
    // Greedy debt simplification
    // Pairs the biggest ower with the biggest receiver each round
    // → minimum number of transactions to settle all debts
    fun simplifyDebts(
        netBalances: Map<String, Double>
    ): List<SettlementSuggestion> {

        val result = mutableListOf<SettlementSuggestion>()

        // Who owes money — negative balance, flipped to positive for easy math
        val debtors = netBalances
            .filter { it.value < -0.01 }
            .map    { it.key to -it.value }
            .sortedByDescending { it.second }

        // Who should receive money — positive balance
        val creditors = netBalances
            .filter { it.value > 0.01 }
            .map    { it.key to it.value }
            .sortedByDescending { it.second }

        // Mutable remaining amounts — indices match debtors/creditors lists
        val debtLeft   = debtors  .map { it.second }.toMutableList()
        val creditLeft = creditors.map { it.second }.toMutableList()

        var d = 0   // debtor pointer
        var c = 0   // creditor pointer

        while (d < debtors.size && c < creditors.size) {

            // Pay as much as possible in this pairing
            val amount = minOf(debtLeft[d], creditLeft[c])

            result.add(
                SettlementSuggestion(
                    fromUserId = debtors[d].first,
                    toUserId = creditors[c].first,
                    amount = amount
                )
            )

            debtLeft[d]   -= amount
            creditLeft[c] -= amount

            // Move pointer when fully settled
            if (debtLeft[d]   < 0.01) d++
            if (creditLeft[c] < 0.01) c++
        }

        return result
    }
}