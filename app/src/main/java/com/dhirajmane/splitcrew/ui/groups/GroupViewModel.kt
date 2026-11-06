package com.dhirajmane.splitcrew.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dhirajmane.splitcrew.data.ExpenseEntity
import com.dhirajmane.splitcrew.data.ExpenseParticipantEntity
import com.dhirajmane.splitcrew.data.GroupEntity
import com.dhirajmane.splitcrew.data.GroupRepository
import com.dhirajmane.splitcrew.data.MemberEntity
import com.dhirajmane.splitcrew.data.SettlementEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GroupViewModel(private val repository: GroupRepository) : ViewModel() {

    var selectedGroupId: Long? = null

    val groups: StateFlow<List<GroupEntity>> = repository.getAllGroups()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val membersCache = mutableMapOf<Long, StateFlow<List<MemberEntity>>>()
    private val activeMembersCache = mutableMapOf<Long, StateFlow<List<MemberEntity>>>()
    private val expensesCache = mutableMapOf<Long, StateFlow<List<ExpenseEntity>>>()
    private val participantsCache = mutableMapOf<Long, StateFlow<List<ExpenseParticipantEntity>>>()
    private val settlementsCache = mutableMapOf<Long, StateFlow<List<SettlementEntity>>>()

    fun createGroup(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.createGroup(name.trim())
        }
    }

    fun renameGroup(group: GroupEntity, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            repository.renameGroup(group, newName.trim())
        }
    }

    fun deleteGroup(group: GroupEntity) {
        viewModelScope.launch {
            repository.deleteGroup(group)
        }
    }

    fun getMembers(groupId: Long): StateFlow<List<MemberEntity>> =
        membersCache.getOrPut(groupId) {
            repository.getMembers(groupId)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList()
                )
        }

    fun getActiveMembers(groupId: Long): StateFlow<List<MemberEntity>> =
        activeMembersCache.getOrPut(groupId) {
            repository.getActiveMembers(groupId)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList()
                )
        }

    fun addMember(
        groupId: Long,
        name: String,
        onRemovedMatchFound: (MemberEntity) -> Unit
    ) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val match = repository.findRemovedMatch(groupId, name)
            if (match != null) {
                onRemovedMatchFound(match)
            } else {
                repository.addMember(groupId, name.trim())
            }
        }
    }

    fun restoreMember(memberId: Long) {
        viewModelScope.launch {
            repository.restoreMember(memberId)
        }
    }

    fun addNewMember(groupId: Long, name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.addMember(groupId, name.trim())
        }
    }

    fun renameMember(member: MemberEntity, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            repository.renameMember(member, newName.trim())
        }
    }

    fun removeMember(memberId: Long) {
        viewModelScope.launch {
            repository.markMemberRemoved(memberId)
        }
    }

    fun getExpenses(groupId: Long): StateFlow<List<ExpenseEntity>> =
        expensesCache.getOrPut(groupId) {
            repository.getExpenses(groupId)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList()
                )
        }

    fun getParticipants(groupId: Long): StateFlow<List<ExpenseParticipantEntity>> =
        participantsCache.getOrPut(groupId) {
            repository.getParticipants(groupId)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList()
                )
        }

    fun getSettlements(groupId: Long): StateFlow<List<SettlementEntity>> =
        settlementsCache.getOrPut(groupId) {
            repository.getSettlements(groupId)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList()
                )
        }

    fun recordSettlement(groupId: Long, fromMemberId: Long, toMemberId: Long, amountInCents: Long) {
        if (amountInCents <= 0L || fromMemberId == toMemberId) return
        viewModelScope.launch {
            val activeIds = repository.getActiveMemberIdsNow(groupId)
            if (fromMemberId !in activeIds || toMemberId !in activeIds) return@launch
            repository.addSettlement(groupId, fromMemberId, toMemberId, amountInCents)
        }
    }

    // Undo works only on pending payments. The data layer also enforces this.
    fun undoSettlement(settlement: SettlementEntity) {
        if (settlement.isConfirmed) return
        viewModelScope.launch {
            repository.deletePendingSettlement(settlement)
        }
    }

    // Done confirms the payment. It becomes read-only and cannot be undone.
    fun confirmSettlement(settlement: SettlementEntity) {
        if (settlement.isConfirmed) return
        viewModelScope.launch {
            repository.confirmSettlement(settlement)
        }
    }

    fun addExpense(
        groupId: Long,
        paidByMemberId: Long,
        description: String,
        amountInCents: Long
    ) {
        if (description.isBlank() || amountInCents <= 0L) return
        viewModelScope.launch {
            val activeIds = repository.getActiveMemberIdsNow(groupId)
            if (paidByMemberId !in activeIds) return@launch

            val expenseId = repository.addExpense(
                groupId, paidByMemberId, description.trim(), amountInCents
            )
            repository.addParticipants(expenseId, activeIds)
        }
    }

    fun updateExpense(
        expense: ExpenseEntity,
        paidByMemberId: Long,
        description: String,
        amountInCents: Long
    ) {
        if (description.isBlank() || amountInCents <= 0L) return
        viewModelScope.launch {
            val activeIds = repository.getActiveMemberIdsNow(expense.groupId)
            if (paidByMemberId !in activeIds) return@launch

            repository.updateExpense(
                expense = expense,
                newPayerId = paidByMemberId,
                newDescription = description.trim(),
                newAmountInCents = amountInCents
            )
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    companion object {
        fun factory(repository: GroupRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return GroupViewModel(repository) as T
                }
            }
    }
}