package com.example.splitbuddy.ui.home_screen.settlement

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splitbuddy.R
import com.example.splitbuddy.ui.components.InitialsAvatar
import com.example.splitbuddy.ui.components.ScreenStateWrapper
import com.example.splitbuddy.ui.components.SplitBuddyCard
import com.example.splitbuddy.ui.theme.Primary
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettlementScreen(groupId: String) {

    val viewModel: SettlementViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val toastMsg = stringResource(R.string.toast_settlement_recorded)

    LaunchedEffect(Unit) { viewModel.load(groupId) }

    LaunchedEffect(state.isSettlementRecorded) {
        if (state.isSettlementRecorded) {
            Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()        }
    }

    state.confirmDialog?.let { dialog ->
        ConfirmSettlementDialog(
            dialog = dialog,
            isSaving = state.isSaving,
            onNoteChange = viewModel::onNoteChange,
            onDismiss = viewModel::onDismissDialog,
            onConfirm = viewModel::onConfirmSettlement
        )
    }

    state.unsettleDialog?.let { dialog ->
        UnsettleDialog(
            dialog    = dialog,
            isSaving  = state.isSaving,
            onDismiss = viewModel::onDismissUnsettleDialog,
            onConfirm = viewModel::onConfirmUnsettle
        )
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = { viewModel.load(groupId, isRefresh = true) }
    ) {
        ScreenStateWrapper(
            isLoading = state.isLoading,
            isEmpty = state.suggestions.isEmpty(),
            emptyMessage = "All settled! 🎉\nNo pending payments in this group"
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 12.dp
                )
            ) {
                items(state.suggestions) { item ->
                    SuggestionCard(
                        item       = item,
                        onMarkPaid = { viewModel.onMarkPaidClick(item) },
                        onUnsettle = { viewModel.onUnsettleClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestionCard(
    item: SuggestionItem,
    onMarkPaid: () -> Unit,
    onUnsettle: () -> Unit
) {
    val nameColor = if (item.isPaid)
        MaterialTheme.colorScheme.surfaceVariant
    else
        MaterialTheme.colorScheme.onSurface

    val amountColor = if (item.isPaid)
        MaterialTheme.colorScheme.surfaceVariant
    else
        Primary

    SplitBuddyCard(
        dimmed = item.isPaid,
        elevation = if (item.isPaid) 2.dp else 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {

            // ── Row 1: Avatars + Arrow ────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // From avatar — left aligned
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    InitialsAvatar(name = item.fromName, size = 34.dp)
                }

                // Arrow + amount in the center
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "₹${"%.2f".format(item.amount)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = amountColor
                    )
                    Text(
                        text = "———→",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                // To avatar — right aligned
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    InitialsAvatar(name = item.toName, size = 34.dp)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ── Row 2: Names ──────────────────────────────────────────────
            Row(modifier = Modifier.fillMaxWidth()) {
                // From name — left
                Text(
                    text = item.fromName,
                    fontSize = 14.sp,
                    color = nameColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )

                // Empty center spacer to align under arrow
                Spacer(modifier = Modifier.weight(0.5f))

                // To name — right
                Text(
                    text = item.toName,
                    fontSize = 14.sp,
                    color = nameColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── Row 3: Button ─────────────────────────────────────────────
            Button(
                onClick  = if (item.isPaid) onUnsettle else onMarkPaid,
                enabled  = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.3f)
                )
            ) {
                Text(
                    text = if (item.isPaid) "Paid ✓" else "Mark Paid",
                    color = if (item.isPaid)
                        MaterialTheme.colorScheme.surfaceVariant
                    else
                        Color.White
                )
            }
        }
    }
}

@Composable
private fun ConfirmSettlementDialog(
    dialog: ConfirmDialogState,
    isSaving: Boolean,
    onNoteChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(text = "Confirm Settlement", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    text = "${dialog.fromName} pays ${dialog.toName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "₹${"%.2f".format(dialog.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = dialog.note,
                    onValueChange = onNoteChange,
                    label = { Text("Note (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isSaving,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isSaving,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Confirm")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text("Cancel")
            }
        }
    )
}
@Composable
private fun UnsettleDialog(
    dialog: UnsettleDialogState,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text       = "Unsettle Payment",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text  = "Remove settlement between ${dialog.fromName} and ${dialog.toName}?",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text       = "₹${"%.2f".format(dialog.amount)}",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = Primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text     = "This will mark the payment as pending again.",
                    fontSize = 12.sp,
                    color    = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isSaving,
                shape   = RoundedCornerShape(10.dp),
                colors  = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC62828)   // red — destructive action
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(18.dp),
                        color       = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Unsettle", color = Color.White)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text("Cancel")
            }
        }
    )
}