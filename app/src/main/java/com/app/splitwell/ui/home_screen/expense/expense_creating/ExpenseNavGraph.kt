package com.app.splitwell.ui.home_screen.expense.expense_creating

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.app.splitwell.ui.home_screen.Screen
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.expenseGraph(navController: NavController) {

    composable(Screen.ExpenseScreen1.route + "/{groupId}") { backStackEntry ->

        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""

        // ViewModel lives here — Screen 1 owns it
        val viewModel: ExpenseViewModel = koinViewModel()
        val state = viewModel.state.collectAsState()

        LaunchedEffect(groupId) {
            viewModel.init(groupId)
        }

        ExpenseScreen1(
            state = state.value,
            onTitleChange = viewModel::onTitleChange,
            onAmountChange = viewModel::onAmountChange,
            onPaidByChange = viewModel::onPaidByChange,
            onDescriptionChange = viewModel::onDescriptionChange,
            onNext = {
                val success = viewModel.goToSplitScreen()
                if (success) {
                    navController.navigate(Screen.ExpenseScreen2.route + "/$groupId")
                }
            }
        )
    }

    composable(Screen.ExpenseScreen2.route + "/{groupId}") { backStackEntry ->

        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""

        // 👇 Get the Screen 1 back stack entry — so we reuse ITS ViewModel
        val screen1Entry = remember(backStackEntry) {
            navController.getBackStackEntry(Screen.ExpenseScreen1.route + "/$groupId")
        }
        val viewModel: ExpenseViewModel = koinViewModel(viewModelStoreOwner = screen1Entry)
        val state = viewModel.state.collectAsState()

        ExpenseScreen2(
            state = state.value,
            onSplitMethodChange = viewModel::onSplitMethodChange,
            onSplitAmountChange = viewModel::onSplitAmountChange,
            onPercentChange = viewModel::onPercentChange,
            onUserToggle = viewModel::onUserToggle,   // ← add this line
            onNext = {
                val success = viewModel.goToPreviewScreen()
                if (success) {
                    navController.navigate(Screen.ExpenseScreen3.route + "/$groupId")
                }
            }
        )
    }

    composable(Screen.ExpenseScreen3.route + "/{groupId}") { backStackEntry ->

        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""

        // 👇 Same — reuse Screen 1's ViewModel
        val screen1Entry = remember(backStackEntry) {
            navController.getBackStackEntry(Screen.ExpenseScreen1.route + "/$groupId")
        }
        val viewModel: ExpenseViewModel = koinViewModel(viewModelStoreOwner = screen1Entry)
        val state = viewModel.state.collectAsState()

        ExpenseScreen3(
            state = state.value,
            onSave = {
                viewModel.saveExpense(groupId)
                navController.navigate(Screen.GroupScreen.createRoute(groupId)) {
                    popUpTo(Screen.ExpenseScreen1.route + "/$groupId") {
                        inclusive = true
                    }
                }
            }
        )
    }
}