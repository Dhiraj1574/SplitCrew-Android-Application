package com.dhirajmane.splitcrew.data

import kotlinx.coroutines.flow.Flow

class GroupRepository(private val groupDao: GroupDao) {

    fun getAllGroups(): Flow<List<GroupEntity>> = groupDao.getAllGroups()

    suspend fun createGroup(name: String): Long =
        groupDao.insertGroup(GroupEntity(name = name))

    suspend fun renameGroup(group: GroupEntity, newName: String) =
        groupDao.updateGroup(group.copy(name = newName))

    suspend fun deleteGroup(group: GroupEntity) =
        groupDao.deleteGroup(group)

    suspend fun addMember(groupId: Long, name: String): Long =
        groupDao.insertMember(MemberEntity(groupId = groupId, name = name))

    // Returns the removed member with the same name (ignoring case and extra spaces), if any
    suspend fun findRemovedMatch(groupId: Long, name: String): MemberEntity? {
        val key = name.trim().lowercase()
        return groupDao.getRemovedMembersNow(groupId)
            .firstOrNull { it.name.trim().lowercase() == key }
    }

    suspend fun restoreMember(memberId: Long) =
        groupDao.restoreMember(memberId)

    suspend fun renameMember(member: MemberEntity, newName: String) =
        groupDao.updateMember(member.copy(name = newName))

    fun getMembers(groupId: Long): Flow<List<MemberEntity>> =
        groupDao.getMembers(groupId)

    fun getActiveMembers(groupId: Long): Flow<List<MemberEntity>> =
        groupDao.getActiveMembers(groupId)

    suspend fun getActiveMemberIdsNow(groupId: Long): List<Long> =
        groupDao.getActiveMemberIdsNow(groupId)

    suspend fun markMemberRemoved(memberId: Long) =
        groupDao.markMemberRemoved(memberId)

    suspend fun addExpense(
        groupId: Long,
        paidByMemberId: Long,
        description: String,
        amountInCents: Long
    ): Long = groupDao.insertExpense(
        ExpenseEntity(
            groupId = groupId,
            paidByMemberId = paidByMemberId,
            description = description,
            amountInCents = amountInCents
        )
    )

    suspend fun updateExpense(
        expense: ExpenseEntity,
        newPayerId: Long,
        newDescription: String,
        newAmountInCents: Long
    ) {
        groupDao.updateExpense(
            expense.copy(
                paidByMemberId = newPayerId,
                description = newDescription,
                amountInCents = newAmountInCents
            )
        )
    }

    suspend fun deleteExpense(expense: ExpenseEntity) {
        groupDao.deleteParticipantsForExpense(expense.id)
        groupDao.deleteExpense(expense)
    }

    fun getExpenses(groupId: Long): Flow<List<ExpenseEntity>> =
        groupDao.getExpenses(groupId)

    suspend fun addParticipants(expenseId: Long, memberIds: List<Long>) =
        groupDao.insertParticipants(
            memberIds.map { ExpenseParticipantEntity(expenseId = expenseId, memberId = it) }
        )

    fun getParticipants(groupId: Long): Flow<List<ExpenseParticipantEntity>> =
        groupDao.getParticipants(groupId)

    suspend fun addSettlement(
        groupId: Long,
        fromMemberId: Long,
        toMemberId: Long,
        amountInCents: Long
    ): Long = groupDao.insertSettlement(
        SettlementEntity(
            groupId = groupId,
            fromMemberId = fromMemberId,
            toMemberId = toMemberId,
            amountInCents = amountInCents
        )
    )

    // Undo is only possible while a payment is still pending
    suspend fun deletePendingSettlement(settlement: SettlementEntity) =
        groupDao.deletePendingSettlement(settlement.id)

    // Done locks the payment as final and read-only
    suspend fun confirmSettlement(settlement: SettlementEntity) =
        groupDao.confirmSettlement(settlement.id)

    fun getSettlements(groupId: Long): Flow<List<SettlementEntity>> =
        groupDao.getSettlements(groupId)
}