package com.app.splitwell.ui.home_screen.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.splitwell.ui.components.SplitWellCard
import com.app.splitwell.ui.util.toTimeAgo
import com.app.splitwell.ui.theme.Primary
import com.app.splitwell.R

@Composable
fun ExpenseListCard(
    title: String,
    by: String,
    amount: Double,
    createdAt: String = "",
    onClick: () -> Unit = {}
) {
    SplitWellCard(elevation = 8.dp, onClick = onClick) {
        Row(modifier = Modifier.fillMaxWidth()) {

            // Left accent bar
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(72.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                    .background(Primary)
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.by),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = by,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.amount_rupee_format, "%.2f".format(amount)),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Split type chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Primary.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = createdAt.toTimeAgo(),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }
    }
}