package com.dhirajmane.splitcrew.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {

    @Insert
    suspend fun insertGroup(group: GroupEntity): Long

    @Query("SELECT * FROM `groups` ORDER BY createdAt DESC")
    fun getAllGroups(): Flow<List<GroupEntity>>

    @Update
    suspend fun updateGroup(group: GroupEntity)

    @Delete
    suspend fun deleteGroup(group: GroupEntity)

    @Insert
    suspend fun insertMember(member: MemberEntity): Long

    @Query("SELECT * FROM members WHERE groupId = :groupId")
    fun getMembers(groupId: Long): Flow<List<MemberEntity>>

    @Query("SELECT * FROM members WHERE groupId = :groupId AND isRemoved = 0")
    fun getActiveMembers(groupId: Long): Flow<List<MemberEntity>>

    @Query("SELECT id FROM members WHERE groupId = :groupId AND isRemoved = 0")
    suspend fun getActiveMemberIdsNow(groupId: Long): List<Long>

    @Query("SELECT * FROM members WHERE groupId = :groupId AND isRemoved = 1")
    suspend fun getRemovedMembersNow(groupId: Long): List<MemberEntity>

    @Update
    suspend fun updateMember(member: MemberEntity)

    @Query("UPDATE members SET isRemoved = 1 WHERE id = :memberId")
    suspend fun markMemberRemoved(memberId: Long)

    @Query("UPDATE members SET isRemoved = 0 WHERE id = :memberId")
    suspend fun restoreMember(memberId: Long)

    @Insert
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expense_participants WHERE expenseId = :expenseId")
    suspend fun deleteParticipantsForExpense(expenseId: Long)

    @Query("SELECT * FROM expenses WHERE groupId = :groupId ORDER BY createdAt DESC")
    fun getExpenses(groupId: Long): Flow<List<ExpenseEntity>>

    @Insert
    suspend fun insertParticipants(participants: List<ExpenseParticipantEntity>)

    @Query(
        "SELECT ep.* FROM expense_participants ep " +
                "INNER JOIN expenses e ON ep.expenseId = e.id " +
                "WHERE e.groupId = :groupId"
    )
    fun getParticipants(groupId: Long): Flow<List<ExpenseParticipantEntity>>

    @Insert
    suspend fun insertSettlement(settlement: SettlementEntity): Long

    // Only unconfirmed payments can be deleted. Confirmed ones stay in history forever.
    @Query("DELETE FROM settlements WHERE id = :settlementId AND isConfirmed = 0")
    suspend fun deletePendingSettlement(settlementId: Long)

    @Query("UPDATE settlements SET isConfirmed = 1 WHERE id = :settlementId")
    suspend fun confirmSettlement(settlementId: Long)

    @Query("SELECT * FROM settlements WHERE groupId = :groupId ORDER BY createdAt DESC")
    fun getSettlements(groupId: Long): Flow<List<SettlementEntity>>
}