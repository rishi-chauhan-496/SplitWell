package com.example.splitbuddy.ui.home_screen

sealed class Screen(val route: String) {
    object Groups : Screen("groups")

    object GroupScreen : Screen("groupScreen/{groupId}") {
        fun createRoute(groupId: String) = "groupScreen/$groupId"
    }
    object GroupUpdatingScreen : Screen("groupUpdate/{groupId}") {
        fun createRoute(groupId: String) = "groupUpdate/$groupId"
    }
    object GroupCreationScreen : Screen("groupCreation")

    object AddMemberScreen : Screen("addMember/{groupId}") {
        fun createRoute(groupId: String) = "addMember/$groupId"
    }

    object ExpenseDetailScreen : Screen("expenseDetail/{expenseId}/{groupId}") {
        fun createRoute(expenseId: String, groupId: String) = "expenseDetail/$expenseId/$groupId"
    }

    object ExpenseUpdateScreen : Screen("expenseUpdate/{expenseId}/{groupId}") {
        fun createRoute(expenseId: String, groupId: String) = "expenseUpdate/$expenseId/$groupId"
    }

    object ExpenseScreen1 : Screen("Expense_Screen_1")
    object ExpenseScreen2 : Screen("Expense_Screen_2")
    object ExpenseScreen3 : Screen("Expense_Screen_3")

    object ProfileEditScreen : Screen("profileEdit")
}