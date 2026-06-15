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
import androidx.compose.ui.res.stringResource
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
import com.example.splitbuddy.ui.home_screen.settlement.SettlementScreen
import com.example.splitbuddy.ui.home_screen.top_bar.AppTopBar
import com.example.splitbuddy.ui.home_screen.top_bar.TopBarState
import com.example.splitbuddy.ui.home_screen.top_bar.TopBarViewModel
import com.example.splitbuddy.ui.profile.ProfileEditScreen
import com.example.splitbuddy.ui.theme.SplitBuddyTheme
import org.koin.androidx.compose.koinViewModel
import androidx.core.content.edit
import com.example.splitbuddy.R
import androidx.navigation.navDeepLink
import com.example.splitbuddy.ui.invite.InvitePreviewScreen
import com.example.splitbuddy.ui.home_screen.dashboard.DashboardScreen
import com.example.splitbuddy.ui.home_screen.friend.FriendListScreen
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

    // Called when app is already running and receives a new deep link
    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)   // updates the intent so NavHost picks it up
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
    val titleDashboard = stringResource(R.string.topbar_dashboard)
    val titleFriends   = stringResource(R.string.topbar_friends)
    val strGroups        = stringResource(R.string.topbar_groups)
    val strGroupDetails  = stringResource(R.string.topbar_group_details)
    val strUpdateGroup   = stringResource(R.string.topbar_update_group)
    val strCreateGroup   = stringResource(R.string.topbar_create_group)
    val strExpenseDetail  = stringResource(R.string.topbar_expense_detail)  // ← added
    val strEditExpense    = stringResource(R.string.topbar_expense_update)
    val strAddMembers    = stringResource(R.string.topbar_add_members)
    val strAddExpense    = stringResource(R.string.topbar_add_expense)
    val strSplit         = stringResource(R.string.topbar_split)
    val strPreview       = stringResource(R.string.topbar_preview)
    val strMyProfile     = stringResource(R.string.topbar_my_profile)
    val strEditProfile   = stringResource(R.string.topbar_edit_profile)

    val snackbarHostState = remember { SnackbarHostState() }
    val strSettlement = stringResource(R.string.topbar_settlement)

    val strInvitePreview = stringResource(R.string.topbar_invite_preview)

    LaunchedEffect(route) {
        when {
            route == Screen.Groups.route -> {
                topBarViewModel.update(
                    TopBarState(title = strGroups, isVisible = true)
                )
            }

            route?.startsWith(Screen.GroupScreen.route) == true -> {
                val groupId = navBackStackEntry?.arguments?.getString("groupId") ?: ""
                topBarViewModel.update(
                    TopBarState(
                        title = strGroupDetails,
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
                    TopBarState(title = strUpdateGroup, isVisible = true, showBack = true)
                )
            }

            route == Screen.GroupCreationScreen.route -> {
                topBarViewModel.update(
                    TopBarState(title = strCreateGroup, isVisible = true, showBack = true)
                )
            }

            route?.startsWith("addMember/") == true -> {
                topBarViewModel.update(
                    TopBarState(title = strAddMembers, isVisible = true, showBack = true)
                )
            }

            route?.startsWith("expenseDetail/") == true -> {
                val expenseId = navBackStackEntry?.arguments?.getString("expenseId") ?: ""
                val groupId = navBackStackEntry?.arguments?.getString("groupId") ?: ""
                topBarViewModel.update(
                    TopBarState(
                        title = strExpenseDetail,
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
                    TopBarState(title = strEditExpense, isVisible = true, showBack = true)
                )
            }

            route?.startsWith(Screen.ExpenseScreen1.route) == true -> {
                topBarViewModel.update(
                    TopBarState(
                        title = strAddExpense,
                        isVisible = true,
                        showBack = true
                    )
                )
            }

            route?.startsWith(Screen.ExpenseScreen2.route) == true -> {
                topBarViewModel.update(
                    TopBarState(
                        title = strSplit,
                        isVisible = true,
                        showBack = true
                    )
                )
            }

            route?.startsWith(Screen.ExpenseScreen3.route) == true -> {
                topBarViewModel.update(
                    TopBarState(
                        title = strPreview,
                        isVisible = true,
                        showBack = true
                    )
                )
            }

            route == BottomNavItem.Profile.route -> {
                topBarViewModel.update(
                    TopBarState(
                        title = strMyProfile,
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
                        title = strEditProfile,
                        isVisible = true,
                        showBack = true
                    )
                )
            }

            route == BottomNavItem.Dashboard.route -> {
                topBarViewModel.update(
                    TopBarState(title = titleDashboard, isVisible = true)
                )
            }

            route == BottomNavItem.FriendList.route -> {
                topBarViewModel.update(
                    TopBarState(title = titleFriends, isVisible = true)
                )
            }

            route?.startsWith("settlement/") == true -> {
                topBarViewModel.update(
                    TopBarState(title = strSettlement, isVisible = true, showBack = true)
                )
            }

            route?.startsWith("invitePreview/") == true -> {
                topBarViewModel.update(
                    TopBarState(
                        title    = strInvitePreview,
                        isVisible = true,
                        showBack  = true
                    )
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

            composable(BottomNavItem.FriendList.route) {
                FriendListScreen(ownerID = ownerID)
            }

            composable(Screen.GroupScreen.route) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
                GroupScreen(
                    groupId = groupId,
                    ownerID = ownerID,
                    onAddExpense = {
                        navController.navigate(Screen.ExpenseScreen1.route + "/$groupId")
                    },
                    onAddMember = {
                        navController.navigate(Screen.AddMemberScreen.createRoute(groupId))
                    },
                    onSettlement = {
                        navController.navigate(Screen.SettlementScreen.createRoute(groupId))
                    },
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

            composable(Screen.SettlementScreen.route) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
                SettlementScreen(groupId = groupId)
            }

            composable(
                route = Screen.InvitePreviewScreen.route,
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "https://paysplit.app/invite/{token}"
                    }
                )
            ) { backStackEntry ->
                val token = backStackEntry.arguments?.getString("token") ?: ""
                InvitePreviewScreen(
                    token       = token,
                    ownerID     = ownerID,
                    onJoinGroup = { groupId ->
                        // Navigate to GroupScreen, clear invite preview from back stack
                        navController.navigate(Screen.GroupScreen.createRoute(groupId)) {
                            popUpTo(Screen.InvitePreviewScreen.route) { inclusive = true }
                        }
                    },
                    onCancel = { navController.popBackStack() }
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