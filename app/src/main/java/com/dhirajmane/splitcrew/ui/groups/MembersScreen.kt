package com.dhirajmane.splitcrew.ui.groups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.dhirajmane.splitcrew.data.MemberEntity
import com.dhirajmane.splitcrew.domain.MemberBalance
import com.dhirajmane.splitcrew.ui.theme.BrandTeal
import com.dhirajmane.splitcrew.ui.theme.GradientHeader
import com.dhirajmane.splitcrew.ui.theme.PremiumCard
import com.dhirajmane.splitcrew.ui.theme.SectionHeader
import com.dhirajmane.splitcrew.ui.theme.StatusPill

@Composable
fun MembersScreen(
    members: List<MemberEntity>,
    activeMembers: List<MemberEntity>,
    balances: List<MemberBalance>,
    onAddMember: (name: String, onRemovedMatchFound: (MemberEntity) -> Unit) -> Unit,
    onRestoreMember: (Long) -> Unit,
    onAddNewMember: (String) -> Unit,
    onRenameMember: (MemberEntity, String) -> Unit,
    onRemoveMember: (Long) -> Unit,
    onInfo: (MemberEntity) -> Unit,
    onBack: () -> Unit
) {
    val newMemberName = remember { mutableStateOf("") }
    val renamingMember = remember { mutableStateOf<MemberEntity?>(null) }
    val renameText = remember { mutableStateOf("") }
    val removingMember = remember { mutableStateOf<MemberEntity?>(null) }
    val matchedRemoved = remember { mutableStateOf<MemberEntity?>(null) }
    val pendingName = remember { mutableStateOf("") }
    val formerMembers = members.filter { m -> activeMembers.none { it.id == m.id } }

    fun submit() {
        val name = newMemberName.value.trim()
        if (name.isEmpty()) return
        newMemberName.value = ""
        pendingName.value = name
        onAddMember(name) { match -> matchedRemoved.value = match }
    }

    matchedRemoved.value?.let { match ->
        AlertDialog(
            onDismissRequest = { matchedRemoved.value = null },
            title = { Text("Member was removed earlier") },
            text = {
                Text(
                    "\"${match.name}\" was removed from this group earlier. " +
                            "Restore them with their old expenses and balances, or add as a new person?"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onRestoreMember(match.id)
                    matchedRemoved.value = null
                }) { Text("Restore") }
            },
            dismissButton = {
                TextButton(onClick = {
                    onAddNewMember(pendingName.value)
                    matchedRemoved.value = null
                }) { Text("Add as new") }
            }
        )
    }

    renamingMember.value?.let { member ->
        AlertDialog(
            onDismissRequest = { renamingMember.value = null },
            title = { Text("Rename member") },
            text = {
                OutlinedTextField(
                    value = renameText.value,
                    onValueChange = { renameText.value = it },
                    singleLine = true,
                    label = { Text("Member name") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onRenameMember(member, renameText.value)
                    renamingMember.value = null
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { renamingMember.value = null }) { Text("Cancel") }
            }
        )
    }

    removingMember.value?.let { member ->
        ConfirmDialog(
            title = "Remove ${member.name}?",
            message = "They will move to Former members. Past expenses and balances stay unchanged, and you can restore them later by adding the same name.",
            confirmText = "Remove",
            onConfirm = {
                onRemoveMember(member.id)
                removingMember.value = null
            },
            onDismiss = { removingMember.value = null }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { OutlinedButton(onClick = onBack) { Text("‹ Back") } }

        item {
            GradientHeader(title = "Members", subtitle = "${activeMembers.size} active")
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newMemberName.value,
                    onValueChange = { newMemberName.value = it },
                    label = { Text("Member name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { submit() }),
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = { submit() }) { Text("Add") }
            }
        }

        if (activeMembers.isEmpty()) {
            item {
                PremiumCard {
                    Text(
                        text = "No members yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Add everyone who shares expenses in this group, starting with yourself.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(activeMembers, key = { "member-${it.id}" }) { member ->
                val memberBalance = balances.find { it.memberId == member.id }
                val balance = memberBalance?.balanceInCents ?: 0L
                PremiumCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = member.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = formatBalance(balance),
                                style = MaterialTheme.typography.bodySmall,
                                color = balanceColor(balance),
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Paid ${centsToText(memberBalance?.paidInCents ?: 0L)} · Share ${centsToText(memberBalance?.shareInCents ?: 0L)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { onInfo(member) }) { Text("Info") }
                        OutlinedButton(onClick = {
                            renameText.value = member.name
                            renamingMember.value = member
                        }) { Text("Rename") }
                        OutlinedButton(onClick = { removingMember.value = member }) { Text("Remove") }
                    }
                }
            }
        }

        if (formerMembers.isNotEmpty()) {
            item { SectionHeader("Former members") }
            items(formerMembers, key = { "former-${it.id}" }) { member ->
                val memberBalance = balances.find { it.memberId == member.id }
                val balance = memberBalance?.balanceInCents ?: 0L
                PremiumCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = member.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = formatBalance(balance),
                                style = MaterialTheme.typography.bodySmall,
                                color = balanceColor(balance),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        StatusPill(text = "Removed", color = BrandTeal)
                    }
                    OutlinedButton(onClick = { onInfo(member) }) { Text("Info") }
                }
            }
        }
    }
}