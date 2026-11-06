package com.dhirajmane.splitcrew

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.dhirajmane.splitcrew.data.ExpenseEntity
import com.dhirajmane.splitcrew.data.GroupEntity
import com.dhirajmane.splitcrew.data.MemberEntity
import com.dhirajmane.splitcrew.domain.calculateBalances
import com.dhirajmane.splitcrew.ui.groups.AddExpenseScreen
import com.dhirajmane.splitcrew.ui.groups.ExpensesScreen
import com.dhirajmane.splitcrew.ui.groups.GroupHubScreen
import com.dhirajmane.splitcrew.ui.groups.GroupViewModel
import com.dhirajmane.splitcrew.ui.groups.GroupsScreen
import com.dhirajmane.splitcrew.ui.groups.MemberInfoScreen
import com.dhirajmane.splitcrew.ui.groups.MembersScreen
import com.dhirajmane.splitcrew.ui.groups.SettleUpScreen
import com.dhirajmane.splitcrew.ui.groups.SettlementsScreen
import com.dhirajmane.splitcrew.ui.groups.SplashScreen
import com.dhirajmane.splitcrew.ui.theme.SplitCrewTheme

enum class GroupScreen { HUB, MEMBERS, EXPENSES, ADD_EXPENSE, SETTLE_UP, HISTORY, MEMBER_INFO }

class MainActivity : ComponentActivity() {

    private val groupViewModel: GroupViewModel by viewModels {
        GroupViewModel.factory(
            AppContainer(applicationContext).groupRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SplitCrewTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val showSplash = remember { mutableStateOf(true) }

                    if (showSplash.value) {
                        SplashScreen(onFinished = { showSplash.value = false })
                    } else {
                        AppContent()
                    }
                }
            }
        }
    }

    @Composable
    private fun AppContent() {
        val selectedGroup = remember { mutableStateOf<GroupEntity?>(null) }
        val currentScreen = remember { mutableStateOf(GroupScreen.HUB) }
        val editingExpense = remember { mutableStateOf<ExpenseEntity?>(null) }
        val infoMember = remember { mutableStateOf<MemberEntity?>(null) }

        val allGroups by groupViewModel.groups.collectAsState()

        LaunchedEffect(allGroups) {
            val savedId = groupViewModel.selectedGroupId
            if (selectedGroup.value == null && savedId != null) {
                selectedGroup.value = allGroups.find { it.id == savedId }
            }
        }

        val selected = selectedGroup.value
        if (selected == null) {
            GroupsScreen(
                viewModel = groupViewModel,
                onGroupClick = {
                    groupViewModel.selectedGroupId = it.id
                    selectedGroup.value = it
                    currentScreen.value = GroupScreen.HUB
                }
            )
        } else {
            val members by groupViewModel.getMembers(selected.id).collectAsState()
            val activeMembers by groupViewModel.getActiveMembers(selected.id).collectAsState()
            val expenses by groupViewModel.getExpenses(selected.id).collectAsState()
            val participants by groupViewModel.getParticipants(selected.id).collectAsState()
            val settlements by groupViewModel.getSettlements(selected.id).collectAsState()
            val balances = calculateBalances(members, expenses, participants, settlements)

            BackHandler {
                when (currentScreen.value) {
                    GroupScreen.HUB -> {
                        groupViewModel.selectedGroupId = null
                        selectedGroup.value = null
                    }
                    GroupScreen.ADD_EXPENSE -> {
                        editingExpense.value = null
                        currentScreen.value = GroupScreen.EXPENSES
                    }
                    GroupScreen.MEMBER_INFO -> currentScreen.value = GroupScreen.MEMBERS
                    else -> currentScreen.value = GroupScreen.HUB
                }
            }

            when (currentScreen.value) {
                GroupScreen.HUB -> GroupHubScreen(
                    groupName = selected.name,
                    onRenameGroup = { newName ->
                        groupViewModel.renameGroup(selected, newName)
                        selectedGroup.value = selected.copy(name = newName.trim())
                    },
                    onMembers = { currentScreen.value = GroupScreen.MEMBERS },
                    onExpenses = { currentScreen.value = GroupScreen.EXPENSES },
                    onSettleUp = { currentScreen.value = GroupScreen.SETTLE_UP },
                    onHistory = { currentScreen.value = GroupScreen.HISTORY },
                    onBack = {
                        groupViewModel.selectedGroupId = null
                        selectedGroup.value = null
                    }
                )
                GroupScreen.MEMBERS -> MembersScreen(
                    members = members,
                    activeMembers = activeMembers,
                    balances = balances,
                    onAddMember = { name, onMatch ->
                        groupViewModel.addMember(selected.id, name, onMatch)
                    },
                    onRestoreMember = { id -> groupViewModel.restoreMember(id) },
                    onAddNewMember = { name -> groupViewModel.addNewMember(selected.id, name) },
                    onRenameMember = { member, newName -> groupViewModel.renameMember(member, newName) },
                    onRemoveMember = { id -> groupViewModel.removeMember(id) },
                    onInfo = { member ->
                        infoMember.value = member
                        currentScreen.value = GroupScreen.MEMBER_INFO
                    },
                    onBack = { currentScreen.value = GroupScreen.HUB }
                )
                GroupScreen.MEMBER_INFO -> {
                    val target = infoMember.value
                    if (target != null) {
                        MemberInfoScreen(
                            member = members.find { it.id == target.id } ?: target,
                            members = members,
                            balances = balances,
                            expenses = expenses,
                            participants = participants,
                            settlements = settlements,
                            onBack = { currentScreen.value = GroupScreen.MEMBERS }
                        )
                    }
                }
                GroupScreen.EXPENSES -> ExpensesScreen(
                    members = members,
                    expenses = expenses,
                    onAddExpense = {
                        editingExpense.value = null
                        currentScreen.value = GroupScreen.ADD_EXPENSE
                    },
                    onEditExpense = { expense ->
                        editingExpense.value = expense
                        currentScreen.value = GroupScreen.ADD_EXPENSE
                    },
                    onDeleteExpense = { expense -> groupViewModel.deleteExpense(expense) },
                    onBack = { currentScreen.value = GroupScreen.HUB }
                )
                GroupScreen.ADD_EXPENSE -> AddExpenseScreen(
                    activeMembers = activeMembers,
                    editingExpense = editingExpense.value,
                    onSave = { expenseId, payerId, desc, cents ->
                        val existing = editingExpense.value
                        if (expenseId != null && existing != null) {
                            groupViewModel.updateExpense(existing, payerId, desc, cents)
                        } else {
                            groupViewModel.addExpense(selected.id, payerId, desc, cents)
                        }
                        editingExpense.value = null
                        currentScreen.value = GroupScreen.EXPENSES
                    },
                    onBack = {
                        editingExpense.value = null
                        currentScreen.value = GroupScreen.EXPENSES
                    }
                )
                GroupScreen.SETTLE_UP -> SettleUpScreen(
                    balances = balances,
                    onPay = { fromId, toId, cents ->
                        groupViewModel.recordSettlement(selected.id, fromId, toId, cents)
                    },
                    onBack = { currentScreen.value = GroupScreen.HUB }
                )
                GroupScreen.HISTORY -> SettlementsScreen(
                    members = members,
                    settlements = settlements,
                    onConfirmSettlement = { settlement -> groupViewModel.confirmSettlement(settlement) },
                    onUndoSettlement = { settlement -> groupViewModel.undoSettlement(settlement) },
                    onBack = { currentScreen.value = GroupScreen.HUB }
                )
            }
        }
    }
}