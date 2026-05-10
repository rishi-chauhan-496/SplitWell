package com.example.splitbuddy.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splitbuddy.ui.components.AppTextField
import com.example.splitbuddy.ui.components.GradientButton
import com.example.splitbuddy.ui.components.InitialsAvatar
import com.example.splitbuddy.ui.components.LoadingView
import com.example.splitbuddy.ui.theme.gradient2
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileEditScreen(
    userId: String,
    onSaved: () -> Unit
) {
    val viewModel: ProfileEditViewModel = koinViewModel()
    val state = viewModel.state.collectAsState()

    LaunchedEffect(userId) {
        viewModel.load(userId)
    }

    LaunchedEffect(state.value.isSaved) {
        if (state.value.isSaved) onSaved()
    }

    if (state.value.isLoading) {
        LoadingView()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(90.dp)
                .background(brush = gradient2, shape = RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            InitialsAvatar(
                name = state.value.firstName.ifBlank { "?" },
                size = 90.dp,
                fontSize = 36.sp,
                backgroundColor = Color.Transparent,
                textColor = Color.White
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = state.value.email,
            color = MaterialTheme.colorScheme.surfaceVariant,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        AppTextField(
            label = "First Name",
            value = state.value.firstName,
            onValueChange = viewModel::onFirstNameChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        AppTextField(
            label = "Last Name",
            value = state.value.lastName,
            onValueChange = viewModel::onLastNameChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        AppTextField(
            label = "Username *",
            value = state.value.userName,
            onValueChange = viewModel::onUserNameChange,
            isError = state.value.userNameError != null,
            errorMessage = state.value.userNameError
        )

        Spacer(modifier = Modifier.height(8.dp))

        AppTextField(
            label = "Phone Number *",
            value = state.value.contact,
            onValueChange = viewModel::onContactChange,
            isError = state.value.contactError != null,
            errorMessage = state.value.contactError
        )

        Spacer(modifier = Modifier.height(8.dp))

        AppTextField(
            label = "Email (from Google)",
            value = state.value.email,
            onValueChange = {},
            readOnly = true
        )

        Text(
            text = "Email is linked to your Google account and cannot be changed",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.surfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        state.value.error?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        GradientButton(
            text = "Save Profile",
            onClick = { viewModel.save(userId) },
            isLoading = state.value.isSaving
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}