package com.example.splitbuddy.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.splitbuddy.data.local.model.TripManager
import com.example.splitbuddy.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaidByDropdown(
    members: List<TripManager>,
    selectedUserId: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = members.find { it.userId == selectedUserId }
        ?.userName?.ifBlank { selectedUserId } ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = selectedLabel ?: "",
            onValueChange = {},
            readOnly = true,
            isError = isError,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Primary,
                unfocusedIndicatorColor = Primary,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            members.forEach { member ->
                DropdownMenuItem(
                    text = { Text(member.userName.ifBlank { member.userId }) },
                    onClick = {
                        onSelect(member.userId)
                        expanded = false
                    }
                )
            }
        }
    }
}