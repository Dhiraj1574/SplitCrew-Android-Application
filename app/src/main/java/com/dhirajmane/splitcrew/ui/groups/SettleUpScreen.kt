package com.dhirajmane.splitcrew.ui.groups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dhirajmane.splitcrew.domain.MemberBalance
import com.dhirajmane.splitcrew.domain.calculateSettlements
import com.dhirajmane.splitcrew.ui.theme.BrandGreen
import com.dhirajmane.splitcrew.ui.theme.GradientHeader
import com.dhirajmane.splitcrew.ui.theme.PremiumCard
import com.dhirajmane.splitcrew.ui.theme.SectionHeader
import com.dhirajmane.splitcrew.ui.theme.SummaryCard

@Composable
fun SettleUpScreen(
    balances: List<MemberBalance>,
    onPay: (fromMemberId: Long, toMemberId: Long, amountInCents: Long) -> Unit,
    onBack: () -> Unit
) {
    val settlements = calculateSettlements(balances)
    val totalToSettle = settlements.sumOf { it.amountInCents }

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
            GradientHeader(title = "Settle up", subtitle = "Who pays whom")
        }

        item {
            SummaryCard(
                label = "Total still to settle",
                value = centsToText(totalToSettle),
                accent = BrandGreen
            )
        }

        item {
            Text(
                text = "Tap Paid once a payment is made. It moves to History, where you confirm it with Done.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item { SectionHeader("Payments") }

        if (settlements.isEmpty()) {
            item {
                PremiumCard {
                    Text(
                        text = "Everyone is settled up",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "No payments are pending. Add an expense to see who owes whom.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(settlements, key = { "settle-${it.fromName}-${it.toName}-${it.amountInCents}" }) { payment ->
                val fromId = balances.find { it.name == payment.fromName }?.memberId
                val toId = balances.find { it.name == payment.toName }?.memberId
                PremiumCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${payment.fromName} pays ${payment.toName}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = centsToText(payment.amountInCents),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BrandGreen
                            )
                        }
                        Button(
                            enabled = fromId != null && toId != null,
                            onClick = {
                                if (fromId != null && toId != null) {
                                    onPay(fromId, toId, payment.amountInCents)
                                }
                            }
                        ) { Text("Paid") }
                    }
                }
            }
        }
    }
}