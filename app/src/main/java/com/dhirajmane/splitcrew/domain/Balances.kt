package com.dhirajmane.splitcrew.domain

import com.dhirajmane.splitcrew.data.ExpenseEntity
import com.dhirajmane.splitcrew.data.ExpenseParticipantEntity
import com.dhirajmane.splitcrew.data.MemberEntity
import com.dhirajmane.splitcrew.data.SettlementEntity

data class MemberBalance(
    val memberId: Long,
    val name: String,
    val paidInCents: Long,
    val shareInCents: Long,
    val balanceInCents: Long
)

// Positive balance = member is owed money. Negative = member owes money.
// Recorded settlements count as payments: the payer's paid total rises and the receiver's share rises.
fun calculateBalances(
    members: List<MemberEntity>,
    expenses: List<ExpenseEntity>,
    participants: List<ExpenseParticipantEntity>,
    settlements: List<SettlementEntity> = emptyList()
): List<MemberBalance> {
    if (members.isEmpty()) return emptyList()

    val paid = mutableMapOf<Long, Long>()
    val share = mutableMapOf<Long, Long>()
    val participantsByExpense = participants.groupBy { it.expenseId }

    // Leftover cents rotate across expenses so no one always gets the extra cent
    var cursor = 0

    for (expense in expenses) {
        paid[expense.paidByMemberId] =
            (paid[expense.paidByMemberId] ?: 0L) + expense.amountInCents

        val sharers = participantsByExpense[expense.id]
            ?.map { it.memberId }
            ?.sorted()
            ?: continue
        if (sharers.isEmpty()) continue

        val n = sharers.size
        val base = expense.amountInCents / n
        val remainder = (expense.amountInCents % n).toInt()

        sharers.forEachIndexed { index, memberId ->
            val offset = Math.floorMod(index - cursor, n)
            val portion = if (offset < remainder) base + 1 else base
            share[memberId] = (share[memberId] ?: 0L) + portion
        }

        cursor = (cursor + remainder) % n
    }

    // A recorded payment: the payer has paid it (counts as paid),
    // and the receiver has received it (counts against what they are owed)
    for (settlement in settlements) {
        paid[settlement.fromMemberId] =
            (paid[settlement.fromMemberId] ?: 0L) + settlement.amountInCents
        share[settlement.toMemberId] =
            (share[settlement.toMemberId] ?: 0L) + settlement.amountInCents
    }

    return members.map { member ->
        val p = paid[member.id] ?: 0L
        val s = share[member.id] ?: 0L
        MemberBalance(
            memberId = member.id,
            name = member.name,
            paidInCents = p,
            shareInCents = s,
            balanceInCents = p - s
        )
    }
}

data class Payment(
    val fromName: String,
    val toName: String,
    val amountInCents: Long
)

// Returns the fewest payments that clear all balances.
fun calculateSettlements(balances: List<MemberBalance>): List<Payment> {
    val debtors = balances
        .filter { it.balanceInCents < 0 }
        .map { it.name to -it.balanceInCents }
        .sortedByDescending { it.second }
        .toMutableList()

    val creditors = balances
        .filter { it.balanceInCents > 0 }
        .map { it.name to it.balanceInCents }
        .sortedByDescending { it.second }
        .toMutableList()

    val payments = mutableListOf<Payment>()
    var d = 0
    var c = 0

    while (d < debtors.size && c < creditors.size) {
        val (debtorName, owed) = debtors[d]
        val (creditorName, due) = creditors[c]
        val amount = minOf(owed, due)

        if (amount > 0) {
            payments.add(Payment(debtorName, creditorName, amount))
        }

        debtors[d] = debtorName to (owed - amount)
        creditors[c] = creditorName to (due - amount)

        if (debtors[d].second == 0L) d++
        if (creditors[c].second == 0L) c++
    }

    return payments
}
