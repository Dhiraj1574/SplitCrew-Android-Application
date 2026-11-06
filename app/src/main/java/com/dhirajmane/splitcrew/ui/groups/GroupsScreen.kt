package com.dhirajmane.splitcrew.ui.groups

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.dhirajmane.splitcrew.data.GroupEntity
import com.dhirajmane.splitcrew.ui.theme.GradientHeader
import com.dhirajmane.splitcrew.ui.theme.PremiumCard

@Composable
fun GroupsScreen(
    viewModel: GroupViewModel,
    onGroupClick: (GroupEntity) -> Unit
) {
    val groups by viewModel.groups.collectAsState()
    val newGroupName = remember { mutableStateOf("") }
    val groupToDelete = remember { mutableStateOf<GroupEntity?>(null) }

    groupToDelete.value?.let { group ->
        ConfirmDialog(
            title = "Delete group?",
            message = "\"${group.name}\" and all its members, expenses and payments will be permanently removed.",
            confirmText = "Delete",
            onConfirm = {
                viewModel.deleteGroup(group)
                groupToDelete.value = null
            },
            onDismiss = { groupToDelete.value = null }
        )
    }

    fun submit() {
        val name = newGroupName.value
        newGroupName.value = ""
        viewModel.createGroup(name)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            GradientHeader(title = "My Groups", subtitle = "Split bills with your crew")
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newGroupName.value,
                    onValueChange = { newGroupName.value = it },
                    label = { Text("New group name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { submit() }),
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = { submit() }) { Text("Add") }
            }
        }

        if (groups.isEmpty()) {
            item {
                PremiumCard {
                    Text(
                        text = "No groups yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Type a name above, like \"Goa Trip\" or \"Flat 4B\", to start splitting bills.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(groups, key = { it.id }) { group ->
                PremiumCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = group.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onGroupClick(group) }
                        )
                        OutlinedButton(onClick = { groupToDelete.value = group }) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}