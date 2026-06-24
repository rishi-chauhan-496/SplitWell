package com.app.splitwell.ui.home_screen

import android.content.SharedPreferences
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.splitwell.ui.home_screen.bottom_bar.BottomNavItem
import com.app.splitwell.ui.home_screen.bottom_bar.BottomNavigationBar
import com.app.splitwell.ui.home_screen.expense.expense_creating.expenseGraph
import com.app.splitwell.ui.home_screen.expense.expense_screen.ExpenseDetailScreen
import com.app.splitwell.ui.home_screen.expense.expense_update_screen.ExpenseUpdateScreen
import com.app.splitwell.ui.home_screen.group.group_add_member_screen.AddMemberScreen
import com.app.splitwell.ui.home_screen.group.group_creation.GroupCreationScreen
import com.app.splitwell.ui.home_screen.group.group_screen.GroupScreen
import com.app.splitwell.ui.home_screen.group.group_updating.GroupUpdatingScreen
import com.app.splitwell.ui.home_screen.group.groups_screen.GroupsScreen
import com.app.splitwell.ui.home_screen.settlement.SettlementScreen
import com.app.splitwell.ui.home_screen.top_bar.AppTopBar
import com.app.splitwell.ui.home_screen.top_bar.TopBarState
import com.app.splitwell.ui.profile.ProfileEditScreen
import com.app.splitwell.ui.theme.SplitWellTheme
import androidx.core.content.edit
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.app.splitwell.ui.login_screen.LoginActivity
import org.koin.compose.koinInject
import com.app.splitwell.R
import androidx.navigation.navDeepLink
import com.app.splitwell.ui.invite.InvitePreviewScreen
import com.app.splitwell.ui.home_screen.dashboard.DashboardScreen
import com.app.splitwell.ui.home_screen.friend.FriendListScreen
import com.app.splitwell.ui.profile.ProfileScreen
import com.app.splitwell.ui.util.SnackbarController

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplitWellTheme {
                MainScreen()
            }
        }
    }

    // Called when app is already running and receives a new deep link
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)   // updates the intent so NavHost picks it up
    }
}

@Composable
fun MainScreen() {

    val sharedPreferences: SharedPreferences = koinInject()
    val context = LocalContext.current

    val ownerID = remember {
        sharedPreferences.getString("userId", "") ?: ""
    }

    val isNewLogin = remember {
        sharedPreferences.getBoolean("isNewLogin", false)
    }

    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val route = navBackStackEntry?.destination?.route
    val titleDashboard = stringResource(R.string.topbar_dashboard)
    val titleFriends = stringResource(R.string.topbar_friends)
    val strGroups = stringResource(R.string.topbar_groups)
    val strGroupDetails = stringResource(R.string.topbar_group_details)
    val strUpdateGroup = stringResource(R.string.topbar_update_group)
    val strCreateGroup = stringResource(R.string.topbar_create_group)
    val strExpenseDetail = stringResource(R.string.topbar_expense_detail)  // ← added
    val strEditExpense = stringResource(R.string.topbar_expense_update)
    val strAddMembers = stringResource(R.string.topbar_add_members)
    val strAddExpense = stringResource(R.string.topbar_add_expense)
    val strSplit = stringResource(R.string.topbar_split)
    val strPreview = stringResource(R.string.topbar_preview)
    val strMyProfile = stringResource(R.string.topbar_my_profile)
    val strEditProfile = stringResource(R.string.topbar_edit_profile)

    val snackbarHostState = remember { SnackbarHostState() }
    val strSettlement = stringResource(R.string.topbar_settlement)

    val strInvitePreview = stringResource(R.string.topbar_invite_preview)

    val topBarState = when {
        route == Screen.Groups.route ->
            TopBarState(title = strGroups, isVisible = true)

        route?.startsWith(Screen.GroupScreen.route) == true -> {
            val groupId = navBackStackEntry?.arguments?.getString("groupId") ?: ""
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
        }

        route == Screen.GroupUpdatingScreen.route ->
            TopBarState(title = strUpdateGroup, isVisible = true, showBack = true)

        route == Screen.GroupCreationScreen.route ->
            TopBarState(title = strCreateGroup, isVisible = true, showBack = true)

        route?.startsWith("addMember/") == true ->
            TopBarState(title = strAddMembers, isVisible = true, showBack = true)

        route?.startsWith("expenseDetail/") == true -> {
            val expenseId = navBackStackEntry?.arguments?.getString("expenseId") ?: ""
            val groupId = navBackStackEntry?.arguments?.getString("groupId") ?: ""
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
        }

        route?.startsWith("expenseUpdate/") == true ->
            TopBarState(title = strEditExpense, isVisible = true, showBack = true)

        route?.startsWith(Screen.ExpenseScreen1.route) == true ->
            TopBarState(title = strAddExpense, isVisible = true, showBack = true)

        route?.startsWith(Screen.ExpenseScreen2.route) == true ->
            TopBarState(title = strSplit, isVisible = true, showBack = true)

        route?.startsWith(Screen.ExpenseScreen3.route) == true ->
            TopBarState(title = strPreview, isVisible = true, showBack = true)

        route == BottomNavItem.Profile.route ->
            TopBarState(
                title = strMyProfile,
                isVisible = true,
                showBack = false,
                actions = {
                    IconButton(
                        onClick = { navController.navigate(Screen.ProfileEditScreen.route) }
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                }
            )

        route == Screen.ProfileEditScreen.route ->
            TopBarState(title = strEditProfile, isVisible = true, showBack = true)

        route == BottomNavItem.Dashboard.route ->
            TopBarState(title = titleDashboard, isVisible = true)

        route == BottomNavItem.FriendList.route ->
            TopBarState(title = titleFriends, isVisible = true)

        route?.startsWith("settlement/") == true ->
            TopBarState(title = strSettlement, isVisible = true, showBack = true)

        route?.startsWith("invitePreview/") == true ->
            TopBarState(title = strInvitePreview, isVisible = true, showBack = true)

        else -> TopBarState(isVisible = false)
    }

    LaunchedEffect(Unit) {
        SnackbarController.events.collect { event ->
            snackbarHostState.showSnackbar(
                message = event.message,
                actionLabel = event.actionLabel,
                duration = SnackbarDuration.Short
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
                    userId = ownerID,
                    onSignOut = {
                        sharedPreferences.edit { clear() }
                        Firebase.auth.signOut()

                        val intent = Intent(context, LoginActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        context.startActivity(intent)
                    }
                )
            }

            composable(BottomNavItem.Dashboard.route) {
                DashboardScreen(
                    userId = ownerID,
                    onGroupClick = { groupId ->
                        navController.navigate(Screen.GroupScreen.createRoute(groupId))
                    },
                    onExpenseClick = { expenseId, groupId ->
                        navController.navigate(
                            Screen.ExpenseDetailScreen.createRoute(
                                expenseId,
                                groupId
                            )
                        )
                    }
                )
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
                    },
                    onUnavailable = { navController.popBackStack() }
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
                        sharedPreferences.edit { putBoolean("isNewLogin", false) }

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
                        uriPattern = "https://pratikprajapati.cloud/invite/{token}"
                    },
                    navDeepLink {
                        uriPattern = "splitwell://invite/{token}"
                    }
                )
            ) { backStackEntry ->
                val token = backStackEntry.arguments?.getString("token") ?: ""
                InvitePreviewScreen(
                    token = token,
                    ownerID = ownerID,
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
    SplitWellTheme {
        MainScreen()
    }
}