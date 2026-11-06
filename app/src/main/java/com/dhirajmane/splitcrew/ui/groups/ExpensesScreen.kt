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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dhirajmane.splitcrew.data.ExpenseEntity
import com.dhirajmane.splitcrew.data.MemberEntity
import com.dhirajmane.splitcrew.ui.theme.BrandBlue
import com.dhirajmane.splitcrew.ui.theme.GradientHeader
import com.dhirajmane.splitcrew.ui.theme.PremiumCard
import com.dhirajmane.splitcrew.ui.theme.SectionHeader
import com.dhirajmane.splitcrew.ui.theme.SummaryCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExpensesScreen(
    members: List<MemberEntity>,
    expenses: List<ExpenseEntity>,
    onAddExpense: () -> Unit,
    onEditExpense: (ExpenseEntity) -> Unit,
    onDeleteExpense: (ExpenseEntity) -> Unit,
    onBack: () -> Unit
) {
    val expenseToDelete = remember { mutableStateOf<ExpenseEntity?>(null) }
    val totalExpenses = expenses.sumOf { it.amountInCents }

    expenseToDelete.value?.let { expense ->
        ConfirmDialog(
            title = "Delete expense?",
            message = "\"${expense.description}\" (${centsToText(expense.amountInCents)}) will be removed and balances will update.",
            confirmText = "Delete",
            onConfirm = {
                onDeleteExpense(expense)
                expenseToDelete.value = null
            },
            onDismiss = { expenseToDelete.value = null }
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
            GradientHeader(title = "Expenses", subtitle = "Everything your group spent")
        }

        item {
            SummaryCard(
                label = "Total expenses",
                value = centsToText(totalExpenses),
                accent = BrandBlue
            )
        }

        item {
            Button(onClick = onAddExpense, modifier = Modifier.fillMaxWidth()) {
                Text("Add expense")
            }
        }

        item { SectionHeader("Recent") }

        if (expenses.isEmpty()) {
            item {
                PremiumCard {
                    Text(
                        text = "No expenses yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tap Add expense to record the first bill, like dinner or fuel.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(expenses, key = { "expense-${it.id}" }) { expense ->
                val payer = members.find { it.id == expense.paidByMemberId }?.name ?: "Unknown"
                PremiumCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = expense.description,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Paid by $payer · ${formatDate(expense.createdAt)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = centsToText(expense.amountInCents),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrandBlue
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { onEditExpense(expense) }) { Text("Edit") }
                        OutlinedButton(onClick = { expenseToDelete.value = expense }) { Text("Delete") }
                    }
                }
            }
        }
    }
}

private fun formatDate(millis: Long): String =
    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(millis))