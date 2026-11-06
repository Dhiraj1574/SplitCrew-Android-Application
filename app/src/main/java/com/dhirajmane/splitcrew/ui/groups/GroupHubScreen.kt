package com.dhirajmane.splitcrew.ui.groups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dhirajmane.splitcrew.ui.theme.GradientHeader
import com.dhirajmane.splitcrew.ui.theme.OweCoral
import com.dhirajmane.splitcrew.ui.theme.OwedAmber

@Composable
fun GroupHubScreen(
    groupName: String,
    onRenameGroup: (String) -> Unit,
    onMembers: () -> Unit,
    onExpenses: () -> Unit,
    onSettleUp: () -> Unit,
    onHistory: () -> Unit,
    onBack: () -> Unit
) {
    val showRename = remember { mutableStateOf(false) }
    val renameText = remember { mutableStateOf(groupName) }

    if (showRename.value) {
        AlertDialog(
            onDismissRequest = { showRename.value = false },
            title = { Text("Rename group") },
            text = {
                OutlinedTextField(
                    value = renameText.value,
                    onValueChange = { renameText.value = it },
                    singleLine = true,
                    label = { Text("Group name") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onRenameGroup(renameText.value)
                    showRename.value = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showRename.value = false }) { Text("Cancel") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onBack) { Text("‹ All groups") }
            TextButton(onClick = {
                renameText.value = groupName
                showRename.value = true
            }) { Text("Rename") }
        }

        GradientHeader(title = groupName, subtitle = "Choose what to open")

        Button(onClick = onMembers, modifier = Modifier.fillMaxWidth()) { Text("Members") }
        Button(onClick = onExpenses, modifier = Modifier.fillMaxWidth()) { Text("Expenses") }
        Button(onClick = onSettleUp, modifier = Modifier.fillMaxWidth()) { Text("Settle up") }
        Button(
            onClick = onHistory,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = Color.White
            )
        ) { Text("History") }
    }
}

fun formatBalance(cents: Long): String {
    val amount = kotlin.math.abs(cents) / 100.0
    val formatted = "%.2f".format(amount)
    return when {
        cents > 0 -> "gets back $formatted"
        cents < 0 -> "owes $formatted"
        else -> "settled up"
    }
}

fun centsToText(cents: Long): String = "%.2f".format(cents / 100.0)

fun balanceColor(cents: Long): Color = when {
    cents > 0 -> OwedAmber
    cents < 0 -> OweCoral
    else -> Color.Unspecified
}