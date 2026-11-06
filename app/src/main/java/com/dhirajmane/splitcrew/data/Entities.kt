package com.dhirajmane.splitcrew.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "members",
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupId: Long,
    val name: String,
    val isRemoved: Boolean = false
)

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupId: Long,
    val paidByMemberId: Long,
    val description: String,
    val amountInCents: Long,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "expense_participants",
    primaryKeys = ["expenseId", "memberId"],
    foreignKeys = [
        ForeignKey(
            entity = ExpenseEntity::class,
            parentColumns = ["id"],
            childColumns = ["expenseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExpenseParticipantEntity(
    val expenseId: Long,
    val memberId: Long
)

// A payment recorded between two members. Once confirmed with Done, it is read-only.
@Entity(
    tableName = "settlements",
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SettlementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupId: Long,
    val fromMemberId: Long,
    val toMemberId: Long,
    val amountInCents: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val isConfirmed: Boolean = false
)