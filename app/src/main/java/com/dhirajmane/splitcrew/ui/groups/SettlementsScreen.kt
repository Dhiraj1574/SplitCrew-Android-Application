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
import com.dhirajmane.splitcrew.data.MemberEntity
import com.dhirajmane.splitcrew.data.SettlementEntity
import com.dhirajmane.splitcrew.ui.theme.BrandGreen
import com.dhirajmane.splitcrew.ui.theme.BrandTeal
import com.dhirajmane.splitcrew.ui.theme.GradientHeader
import com.dhirajmane.splitcrew.ui.theme.PremiumCard
import com.dhirajmane.splitcrew.ui.theme.StatusPill
import com.dhirajmane.splitcrew.ui.theme.SummaryCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettlementsScreen(
    members: List<MemberEntity>,
    settlements: List<SettlementEntity>,
    onConfirmSettlement: (SettlementEntity) -> Unit,
    onUndoSettlement: (SettlementEntity) -> Unit,
    onBack: () -> Unit
) {
    val confirmedSoFar = settlements.filter { it.isConfirmed }.sumOf { it.amountInCents }

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
            GradientHeader(title = "Payment history", subtitle = "Recorded and confirmed payments")
        }

        item {
            SummaryCard(
                label = "Confirmed so far",
                value = centsToText(confirmedSoFar),
                accent = BrandTeal
            )
        }

        item {
            Text(
                text = "Done locks a payment permanently. Undo is only available until then.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (settlements.isEmpty()) {
            item {
                PremiumCard {
                    Text(
                        text = "No payments recorded yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "When someone taps Paid on Settle up, the payment appears here as Pending.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(settlements, key = { "history-${it.id}" }) { settlement ->
                val fromName = members.find { it.id == settlement.fromMemberId }?.name ?: "Unknown"
                val toName = members.find { it.id == settlement.toMemberId }?.name ?: "Unknown"
                PremiumCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "$fromName → $toName",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${centsToText(settlement.amountInCents)} · ${formatDate(settlement.createdAt)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (settlement.isConfirmed) {
                            StatusPill(text = "Confirmed", color = BrandGreen)
                        } else {
                            StatusPill(text = "Pending", color = BrandTeal)
                        }
                    }
                    if (!settlement.isConfirmed) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { onConfirmSettlement(settlement) }) { Text("Done") }
                            OutlinedButton(onClick = { onUndoSettlement(settlement) }) { Text("Undo") }
                        }
                    }
                }
            }
        }
    }
}

private fun formatDate(millis: Long): String =
    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(millis))