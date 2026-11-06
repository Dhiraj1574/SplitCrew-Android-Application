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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dhirajmane.splitcrew.data.ExpenseEntity
import com.dhirajmane.splitcrew.data.ExpenseParticipantEntity
import com.dhirajmane.splitcrew.data.MemberEntity
import com.dhirajmane.splitcrew.data.SettlementEntity
import com.dhirajmane.splitcrew.domain.MemberBalance
import com.dhirajmane.splitcrew.domain.calculateSettlements
import com.dhirajmane.splitcrew.ui.theme.BrandBlue
import com.dhirajmane.splitcrew.ui.theme.BrandGreen
import com.dhirajmane.splitcrew.ui.theme.BrandTeal
import com.dhirajmane.splitcrew.ui.theme.GradientHeader
import com.dhirajmane.splitcrew.ui.theme.PremiumCard
import com.dhirajmane.splitcrew.ui.theme.SectionHeader
import com.dhirajmane.splitcrew.ui.theme.StatusPill
import com.dhirajmane.splitcrew.ui.theme.SummaryCard

@Composable
fun MemberInfoScreen(
    member: MemberEntity,
    members: List<MemberEntity>,
    balances: List<MemberBalance>,
    expenses: List<ExpenseEntity>,
    participants: List<ExpenseParticipantEntity>,
    settlements: List<SettlementEntity>,
    onBack: () -> Unit
) {
    val memberBalance = balances.find { it.memberId == member.id }
    val balance = memberBalance?.balanceInCents ?: 0L
    val paid = memberBalance?.paidInCents ?: 0L
    val share = memberBalance?.shareInCents ?: 0L

    val remaining = calculateSettlements(balances).filter {
        it.fromName == member.name || it.toName == member.name
    }
    val presentExpenseIds = participants
        .filter { it.memberId == member.id }
        .map { it.expenseId }
        .toSet()
    val sortedExpenses = expenses.sortedByDescending { it.createdAt }
    val related = settlements
        .filter { it.fromMemberId == member.id || it.toMemberId == member.id }
        .sortedByDescending { it.createdAt }

    fun nameOf(id: Long) = members.find { it.id == id }?.name ?: "Unknown"

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
            GradientHeader(
                title = member.name,
                subtitle = if (member.isRemoved) "Removed from group" else "Active member"
            )
        }

        item {
            SummaryCard(
                label = "Balance",
                value = formatBalance(balance),
                accent = balanceColor(balance).takeIf { it != Color.Unspecified } ?: BrandBlue
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PremiumCard(modifier = Modifier.weight(1f)) {
                    Text("Paid", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(centsToText(paid), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BrandBlue)
                }
                PremiumCard(modifier = Modifier.weight(1f)) {
                    Text("Share", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(centsToText(share), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BrandBlue)
                }
            }
        }

        item { SectionHeader("Expenses") }

        if (sortedExpenses.isEmpty()) {
            item {
                Text("No expenses in this group yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(sortedExpenses, key = { "info-exp-${it.id}" }) { expense ->
                val present = expense.id in presentExpenseIds
                val isPayer = expense.paidByMemberId == member.id
                PremiumCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(expense.description, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "Paid by ${nameOf(expense.paidByMemberId)}" + if (isPayer) " (this member)" else "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(centsToText(expense.amountInCents), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                    StatusPill(
                        text = if (present) "Present · shared" else "Absent · not shared",
                        color = if (present) BrandGreen else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item { SectionHeader("Payments") }

        if (related.isEmpty()) {
            item {
                Text("No recorded payments.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(related, key = { "info-set-${it.id}" }) { s ->
                val line = if (s.fromMemberId == member.id) "Paid ${nameOf(s.toMemberId)}" else "Received from ${nameOf(s.fromMemberId)}"
                PremiumCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(line, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        Text(centsToText(s.amountInCents), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item { SectionHeader("Still to settle") }

        if (remaining.isEmpty()) {
            item {
                Text("Nothing left to settle.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(remaining, key = { "info-rem-${it.fromName}-${it.toName}-${it.amountInCents}" }) { p ->
                val text = if (p.fromName == member.name) "Pay ${p.toName}" else "Receive from ${p.fromName}"
                PremiumCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        StatusPill(text = centsToText(p.amountInCents), color = BrandTeal)
                    }
                }
            }
        }
    }
}