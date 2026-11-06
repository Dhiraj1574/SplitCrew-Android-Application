package com.dhirajmane.splitcrew.ui.groups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dhirajmane.splitcrew.data.ExpenseEntity
import com.dhirajmane.splitcrew.data.MemberEntity
import com.dhirajmane.splitcrew.ui.theme.GradientHeader
import com.dhirajmane.splitcrew.ui.theme.PremiumCard
import com.dhirajmane.splitcrew.ui.theme.SectionHeader

@Composable
fun AddExpenseScreen(
    activeMembers: List<MemberEntity>,
    editingExpense: ExpenseEntity?,
    onSave: (expenseId: Long?, paidByMemberId: Long, description: String, amountInCents: Long) -> Unit,
    onBack: () -> Unit
) {
    var description by remember(editingExpense) { mutableStateOf(editingExpense?.description ?: "") }
    var amountText by remember(editingExpense) {
        mutableStateOf(editingExpense?.let { "%.2f".format(it.amountInCents / 100.0) } ?: "")
    }
    var paidById by remember(editingExpense) { mutableStateOf(editingExpense?.paidByMemberId) }

    if (paidById != null && activeMembers.none { it.id == paidById }) {
        paidById = null
    }

    val isEditing = editingExpense != null
    val cents = ((amountText.toDoubleOrNull() ?: 0.0) * 100).toLong()
    val canSave = description.isNotBlank() && cents > 0 && paidById != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(onClick = onBack) { Text("‹ Back") }

        GradientHeader(
            title = if (isEditing) "Edit expense" else "Add expense",
            subtitle = "Who paid, and for what"
        )

        PremiumCard {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("What for? (e.g. Dinner)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Amount") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        SectionHeader("Paid by")

        if (activeMembers.isEmpty()) {
            Text(
                text = "Add members first, then choose who paid.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                activeMembers.forEach { member ->
                    Button(onClick = { paidById = member.id }) {
                        Text(if (paidById == member.id) "✓ ${member.name}" else member.name)
                    }
                }
            }
        }

        Button(
            enabled = canSave,
            onClick = {
                val payer = paidById
                if (payer != null && canSave) {
                    onSave(editingExpense?.id, payer, description, cents)
                    description = ""
                    amountText = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEditing) "Update expense" else "Save expense")
        }

        if (!canSave) {
            Text(
                text = "Fill in what for, the amount, and who paid to save.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}