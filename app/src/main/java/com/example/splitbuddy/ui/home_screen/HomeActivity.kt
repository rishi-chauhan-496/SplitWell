package com.example.splitbuddy.ui.home_screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.splitbuddy.ui.home_screen.bottom_bar.BottomNavItem
import com.example.splitbuddy.ui.home_screen.bottom_bar.BottomNavigationBar
import com.example.splitbuddy.ui.home_screen.expense.expense_creating.expenseGraph
import com.example.splitbuddy.ui.home_screen.expense.expense_screen.ExpenseDetailScreen
import com.example.splitbuddy.ui.home_screen.expense.expense_update_screen.ExpenseUpdateScreen
import com.example.splitbuddy.ui.home_screen.group.group_add_member_screen.AddMemberScreen
import com.example.splitbuddy.ui.home_screen.group.group_creation.GroupCreationScreen
import com.example.splitbuddy.ui.home_screen.group.group_screen.GroupScreen
import com.example.splitbuddy.ui.home_screen.group.group_updating.GroupUpdatingScreen
import com.example.splitbuddy.ui.home_screen.group.groups_screen.GroupsScreen
import com.example.splitbuddy.ui.home_screen.top_bar.AppTopBar
import com.example.splitbuddy.ui.home_screen.top_bar.TopBarState
import com.example.splitbuddy.ui.home_screen.top_bar.TopBarViewModel
import com.example.splitbuddy.ui.profile.ProfileEditScreen
import com.example.splitbuddy.ui.theme.SplitBuddyTheme
import org.koin.androidx.compose.koinViewModel
import androidx.core.content.edit
import com.example.splitbuddy.ui.home_screen.dashboard.DashboardScreen
import com.example.splitbuddy.ui.profile.ProfileScreen
import com.example.splitbuddy.ui.util.SnackbarController

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplitBuddyTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {

    val context = LocalContext.current
    val ownerID = remember {
        context.getSharedPreferences("SplitBuddyPrefs", android.content.Context.MODE_PRIVATE)
            .getString("userId", "") ?: ""
    }

    val isNewLogin = remember {
        context.getSharedPreferences("SplitBuddyPrefs", android.content.Context.MODE_PRIVATE)
            .getBoolean("isNewLogin", false)
    }

    val navController = rememberNavController()
    val topBarViewModel: TopBarViewModel = koinViewModel()

    val topBarState by topBarViewModel.state.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val route = navBackStackEntry?.destination?.route

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(route) {
        when {
            route == Screen.Groups.route -> {
                topBarViewModel.update(
                    TopBarState(title = "Groups", isVisible = true)
                )
            }

            route?.startsWith(Screen.GroupScreen.route) == true -> {
                val groupId = navBackStackEntry?.arguments?.getString("groupId") ?: ""
                topBarViewModel.update(
                    TopBarState(
                        title = "Group Details",
                        isVisible = true,
                        showBack = true,
                        actions = {
                            IconButton(
                                onClick = {
                                    navController.navigate(
                                        Screen.GroupUpdatingScreen.createRoute(groupId)
                                    )
                                }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                        }
                    )
                )
            }

            route == Screen.GroupUpdatingScreen.route -> {
                topBarViewModel.update(
                    TopBarState(title = "Update Group", isVisible = true, showBack = true)
                )
            }

            route == Screen.GroupCreationScreen.route -> {
                topBarViewModel.update(
                    TopBarState(title = "Create Group", isVisible = true, showBack = true)
                )
            }

            route?.startsWith("addMember/") == true -> {
                topBarViewModel.update(
                    TopBarState(title = "Add Members", isVisible = true, showBack = true)
                )
            }

            route?.startsWith("expenseDetail/") == true -> {
                val expenseId = navBackStackEntry?.arguments?.getString("expenseId") ?: ""
                val groupId = navBackStackEntry?.arguments?.getString("groupId") ?: ""
                topBarViewModel.update(
                    TopBarState(
                        title = "Expense Detail",
                        isVisible = true,
                        showBack = true,
                        actions = {
                            IconButton(
                                onClick = {
                                    navController.navigate(
                                        Screen.ExpenseUpdateScreen.createRoute(expenseId, groupId)
                                    )
                                }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                        }
                    )
                )
            }

            route?.startsWith("expenseUpdate/") == true -> {
                topBarViewModel.update(
                    TopBarState(title = "Edit Expense", isVisible = true, showBack = true)
                )
            }

            route?.startsWith(Screen.ExpenseScreen1.route) == true -> {
                topBarViewModel.update(
                    TopBarState(
                        title = "Add Expense",
                        isVisible = true,
                        showBack = true
                    )
                )
            }

            route?.startsWith(Screen.ExpenseScreen2.route) == true -> {
                topBarViewModel.update(
                    TopBarState(
                        title = "Split",
                        isVisible = true,
                        showBack = true
                    )
                )
            }

            route?.startsWith(Screen.ExpenseScreen3.route) == true -> {
                topBarViewModel.update(
                    TopBarState(
                        title = "Preview",
                        isVisible = true,
                        showBack = true
                    )
                )
            }

            route == BottomNavItem.Profile.route -> {
                topBarViewModel.update(
                    TopBarState(
                        title = "My Profile",
                        isVisible = true,
                        showBack = false,
                        actions = {
                            IconButton(
                                onClick = {
                                    navController.navigate(Screen.ProfileEditScreen.route)
                                }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                            }
                        }
                    )
                )
            }

            route == Screen.ProfileEditScreen.route -> {
                topBarViewModel.update(
                    TopBarState(
                        title = "Edit Profile",
                        isVisible = true,
                        showBack = true
                    )
                )
            }

            route == BottomNavItem.Dashboard.route -> {
                topBarViewModel.update(
                    TopBarState(title = "Home", isVisible = true)
                )
            }

            else -> {
                topBarViewModel.update(TopBarState(isVisible = false))
            }
        }
    }

    LaunchedEffect(Unit) {
        SnackbarController.events.collect { event ->
            snackbarHostState.showSnackbar(
                message     = event.message,
                actionLabel = event.actionLabel,
                duration    = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (topBarState.isVisible) {
                AppTopBar(
                    title = topBarState.title,
                    showBack = topBarState.showBack,
                    onBackClick = { navController.popBackStack() },
                    actions = topBarState.actions ?: {}
                )
            }
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = if (isNewLogin) Screen.ProfileEditScreen.route
                                else BottomNavItem.Dashboard.route,
            modifier = Modifier.padding(paddingValues)
        ) {

            composable(BottomNavItem.Groups.route) {
                GroupsScreen(
                    userId = ownerID,
                    onNext = { groupId ->
                        navController.navigate(Screen.GroupScreen.createRoute(groupId))
                    },
                    onCreate = {
                        navController.navigate(Screen.GroupCreationScreen.route)
                    }
                )
            }

            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    userId = ownerID
                )
            }

            composable(BottomNavItem.Dashboard.route) {
                DashboardScreen(userId = ownerID)
            }

            composable(Screen.GroupScreen.route) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
                GroupScreen(
                    groupId = groupId,
                    onAddExpense = {
                        navController.navigate(Screen.ExpenseScreen1.route + "/$groupId")
                    },
                    onAddMember = {
                        navController.navigate(Screen.AddMemberScreen.createRoute(groupId))
                    },
                    onSettlement = { /* TODO */ },
                    onExpenseClick = { expenseId ->
                        navController.navigate(
                            Screen.ExpenseDetailScreen.createRoute(expenseId, groupId)
                        )
                    }
                )
            }

            composable(Screen.GroupCreationScreen.route) {
                GroupCreationScreen(
                    userId = ownerID,
                    onGroupCreated = {
                        navController.popBackStack() // go back
                    }
                )
            }

            composable(Screen.GroupUpdatingScreen.route) { backStackEntry ->

                val groupId = backStackEntry.arguments?.getString("groupId") ?: ""

                GroupUpdatingScreen(
                    groupId = groupId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AddMemberScreen.route) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
                AddMemberScreen(
                    groupId = groupId,
                    onBack = { navController.popBackStack() }
                )
            }

            expenseGraph(navController)

            composable(Screen.ExpenseDetailScreen.route) { backStackEntry ->
                val expenseId = backStackEntry.arguments?.getString("expenseId") ?: ""
                ExpenseDetailScreen(
                    expenseId = expenseId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ExpenseUpdateScreen.route) { backStackEntry ->
                val expenseId = backStackEntry.arguments?.getString("expenseId") ?: ""
                val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
                ExpenseUpdateScreen(
                    expenseId = expenseId,
                    groupId = groupId,
                    onBack = {
                        // Pop update screen and detail screen, land on GroupScreen
                        navController.navigate(Screen.GroupScreen.createRoute(groupId)) {
                            popUpTo(Screen.ExpenseDetailScreen.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.ProfileEditScreen.route) {
                ProfileEditScreen(
                    userId = ownerID,
                    onSaved = {
                        context.getSharedPreferences("SplitBuddyPrefs", android.content.Context.MODE_PRIVATE)
                            .edit { putBoolean("isNewLogin", false) }

                        if (isNewLogin) {
                            navController.navigate(BottomNavItem.Groups.route) {
                                popUpTo(Screen.ProfileEditScreen.route) { inclusive = true }
                            }
                        } else {
                            navController.popBackStack()
                        }
                    }
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SplitBuddyTheme {
        MainScreen()
    }
}