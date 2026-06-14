package com.example.splitbuddy.ui.home_screen.group

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
import com.example.splitbuddy.ui.components.SplitBuddyCard
import com.example.splitbuddy.R
import com.example.splitbuddy.ui.util.toTimeAgo
import com.example.splitbuddy.ui.theme.Primary

@Composable
fun GroupListCard(
    groupName: String,
    totalMember: Int,
    totalExpense: Int,
    totalAmount: Double,
    createdAt: String = "",
    onClick: () -> Unit
) {
    SplitBuddyCard(elevation = 8.dp, onClick = onClick) {
        Row(modifier = Modifier.fillMaxWidth()) {

            // Left accent bar
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(80.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                    .background(Primary)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(
                    text = groupName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$totalMember ${stringResource(R.string.member)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text(
                        text = "  ·  ",
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text(
                        text = "$totalExpense ${stringResource(R.string.total_expense)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

            // Amount on the right
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.CenterVertically),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = stringResource(R.string.amount_rupee_format, "%.2f".format(totalAmount)),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                Text(
                    text = createdAt.toTimeAgo(),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}