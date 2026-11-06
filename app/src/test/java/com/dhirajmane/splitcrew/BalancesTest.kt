package com.dhirajmane.splitcrew.domain

import com.dhirajmane.splitcrew.data.ExpenseEntity
import com.dhirajmane.splitcrew.data.ExpenseParticipantEntity
import com.dhirajmane.splitcrew.data.MemberEntity
import com.dhirajmane.splitcrew.data.SettlementEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BalancesTest {

    private val alice = MemberEntity(id = 1, groupId = 1, name = "Alice")
    private val bob = MemberEntity(id = 2, groupId = 1, name = "Bob")
    private val carol = MemberEntity(id = 3, groupId = 1, name = "Carol")
    private val members = listOf(alice, bob, carol)

    private fun expense(id: Long, payer: Long, cents: Long) =
        ExpenseEntity(id = id, groupId = 1, paidByMemberId = payer, description = "e$id", amountInCents = cents)

    private fun shares(expenseId: Long, vararg memberIds: Long) =
        memberIds.map { ExpenseParticipantEntity(expenseId = expenseId, memberId = it) }

    @Test
    fun equalSplit_eachPersonOwesTheirThird() {
        val balances = calculateBalances(
            members,
            listOf(expense(1, payer = 1, cents = 3000)),
            shares(1, 1, 2, 3)
        )
        assertEquals(2000L, balances.first { it.memberId == 1L }.balanceInCents)
        assertEquals(-1000L, balances.first { it.memberId == 2L }.balanceInCents)
        assertEquals(-1000L, balances.first { it.memberId == 3L }.balanceInCents)
    }

    @Test
    fun unevenCents_sharesAddUpExactlyToTheTotal() {
        val balances = calculateBalances(
            members,
            listOf(expense(1, payer = 1, cents = 100)),
            shares(1, 1, 2, 3)
        )
        val totalShare = balances.sumOf { it.shareInCents }
        assertEquals(100L, totalShare)
    }

    @Test
    fun expenseOnlySplitsAmongItsParticipants() {
        // Carol was not a participant, so she owes nothing
        val balances = calculateBalances(
            members,
            listOf(expense(1, payer = 1, cents = 2000)),
            shares(1, 1, 2)
        )
        assertEquals(0L, balances.first { it.memberId == 3L }.shareInCents)
        assertEquals(-1000L, balances.first { it.memberId == 2L }.balanceInCents)
    }

    @Test
    fun recordedSettlement_reducesWhatTheDebtorOwes() {
        val balances = calculateBalances(
            members,
            listOf(expense(1, payer = 1, cents = 3000)),
            shares(1, 1, 2, 3),
            listOf(SettlementEntity(id = 1, groupId = 1, fromMemberId = 2, toMemberId = 1, amountInCents = 1000))
        )
        assertEquals(0L, balances.first { it.memberId == 2L }.balanceInCents)
        assertEquals(1000L, balances.first { it.memberId == 1L }.balanceInCents)
    }

    @Test
    fun settlements_clearAllBalances() {
        val balances = calculateBalances(
            members,
            listOf(expense(1, payer = 1, cents = 10000), expense(2, payer = 2, cents = 4000)),
            shares(1, 1, 2, 3) + shares(2, 1, 2, 3)
        )
        val payments = calculateSettlements(balances)

        // Apply every payment to a running balance and check that all end at zero
        val remaining = balances.associate { it.name to it.balanceInCents }.toMutableMap()
        for (p in payments) {
            remaining[p.fromName] = remaining.getValue(p.fromName) + p.amountInCents
            remaining[p.toName] = remaining.getValue(p.toName) - p.amountInCents
        }
        remaining.values.forEach { assertEquals(0L, it) }
        assertTrue(balances.sumOf { it.balanceInCents } == 0L)
    }

    @Test
    fun allSettled_givesNoPayments() {
        val balances = calculateBalances(members, emptyList(), emptyList())
        assertTrue(calculateSettlements(balances).isEmpty())
    }
}