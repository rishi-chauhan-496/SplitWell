package com.app.splitwell.di

import com.app.splitwell.data.remote.group.GroupApiInterfaceImpl
import com.app.splitwell.data.remote.group.GroupApiInterface
import com.app.splitwell.domain.usecase.group.CreateGroupUseCase
import com.app.splitwell.ui.MainApplication
import com.app.splitwell.data.local.database.Database
import com.app.splitwell.data.local.query.ExpenseQuery
import com.app.splitwell.data.local.query.ExpenseShareQuery
import com.app.splitwell.data.local.query.SettlementQuery
import com.app.splitwell.data.local.query.TripManagerQuery
import com.app.splitwell.data.local.query.TripsQuery
import com.app.splitwell.data.local.query.UserQuery
import com.app.splitwell.data.remote.expense.ExpenseApiInterface
import com.app.splitwell.data.remote.expense.ExpenseApiInterfaceImpl
import com.app.splitwell.data.remote.invite.InviteApiInterface
import com.app.splitwell.data.remote.invite.InviteApiInterfaceImpl
import com.app.splitwell.data.remote.settlement.SettlementApiInterface
import com.app.splitwell.data.remote.settlement.SettlementApiInterfaceImpl
import com.app.splitwell.data.remote.user.UserApiInterface
import com.app.splitwell.data.remote.user.UserApiInterfaceImpl
import com.app.splitwell.data.repository.ExpenseRepositoryImpl
import com.app.splitwell.data.repository.GroupRepositoryImpl
import com.app.splitwell.data.repository.UserRepositoryImpl
import com.app.splitwell.data.sync.AppLifecycleObserver
import com.app.splitwell.data.sync.SyncManager
import com.app.splitwell.domain.calculator.SettlementCalculator
import com.app.splitwell.domain.repository.ExpenseRepository
import com.app.splitwell.domain.repository.GroupRepository
import com.app.splitwell.domain.repository.UserRepository
import com.app.splitwell.domain.usecase.expense.CreateExpenseUseCase
import com.app.splitwell.domain.usecase.expense.DeleteExpenseUseCase
import com.app.splitwell.domain.usecase.expense.GetAllExpenseByGroupIdUseCase
import com.app.splitwell.domain.usecase.expense.UpdateExpenseUseCase
import com.app.splitwell.domain.usecase.group.DeleteGroupUseCase
import com.app.splitwell.domain.usecase.group.GetAllGroupsUseCase
import com.app.splitwell.domain.usecase.group.GetGroupMembersUseCase
import com.app.splitwell.domain.usecase.group.GetGroupUseCase
import com.app.splitwell.domain.usecase.group.UpdateGroupUseCase
import com.app.splitwell.domain.usecase.group.AddMultipleMemberToGroupUseCase
import com.app.splitwell.domain.usecase.group.RemoveMembersFromGroupUseCase
import com.app.splitwell.domain.usecase.invite.AcceptInviteLinkUseCase
import com.app.splitwell.domain.usecase.invite.CreateInviteLinkUseCase
import com.app.splitwell.domain.usecase.invite.PreviewInviteLinkUseCase
import com.app.splitwell.domain.usecase.settlement.CreateSettlementUseCase
import com.app.splitwell.domain.usecase.settlement.DeleteSettlementUseCase
import com.app.splitwell.domain.usecase.settlement.GetGroupBalancesUseCase
import com.app.splitwell.domain.usecase.settlement.GetGroupSettlementsUseCase
import com.app.splitwell.domain.usecase.user.GetAllUserUseCase
import com.app.splitwell.domain.usecase.user.GetOrCreateUserUseCase
import com.app.splitwell.domain.usecase.user.GetUserFriendsUseCase
import com.app.splitwell.domain.usecase.user.UpdateUserUseCase
import com.app.splitwell.domain.usecase.user.GetUserByIdUseCase
import com.app.splitwell.domain.usecase.expense.GetExpenseByIdUseCase
import com.app.splitwell.domain.usecase.expense.GetExpenseSharesByExpenseIdUseCase
import com.app.splitwell.domain.usecase.dashboard.GetDashboardSummaryUseCase
import com.app.splitwell.domain.usecase.group.RefreshGroupUseCase
import com.app.splitwell.domain.usecase.user.GetUserProfileUseCase
import com.app.splitwell.ui.home_screen.dashboard.DashboardViewModel
import com.app.splitwell.ui.home_screen.expense.expense_creating.ExpenseViewModel
import com.app.splitwell.ui.home_screen.expense.expense_screen.ExpenseDetailViewModel
import com.app.splitwell.ui.home_screen.expense.expense_update_screen.ExpenseUpdateViewModel
import com.app.splitwell.ui.home_screen.friend.FriendListViewModel
import com.app.splitwell.ui.home_screen.group.group_add_member_screen.AddMemberViewModel
import com.app.splitwell.ui.home_screen.group.group_screen.GroupDetailViewModel
import com.app.splitwell.ui.home_screen.group.group_updating.GroupUpdatingViewModel
import com.app.splitwell.ui.home_screen.group.group_creation.GroupCreationViewModel
import com.app.splitwell.ui.home_screen.group.groups_screen.GroupsDataViewModel
import com.app.splitwell.ui.home_screen.settlement.SettlementViewModel
import com.app.splitwell.ui.invite.InvitePreviewViewModel
import com.app.splitwell.ui.login_screen.LoginViewModel
import com.app.splitwell.ui.profile.ProfileEditViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

object DIContainer {

    fun main(application: MainApplication) {
        startKoin {
            androidContext(application)
            modules(appModule)
        }
    }

    val appModule = module {

        single<Database> {
            Database(
                context = androidContext()
            )
        }
        single<UserQuery> {
            UserQuery(
                dbHelper = get()
            )
        }
        single<TripsQuery> {
            TripsQuery(
                dbHelper = get()
            )
        }
        single<TripManagerQuery> {
            TripManagerQuery(
                dbHelper = get()
            )
        }
        single<ExpenseQuery> {
            ExpenseQuery(
                dbHelper = get()
            )
        }
        single<ExpenseShareQuery> {
            ExpenseShareQuery(
                dbHelper = get()
            )
        }
        single<SettlementQuery> {
            SettlementQuery(dbHelper = get())
        }

        single<UserApiInterface> {
            UserApiInterfaceImpl()
        }
        single<GroupApiInterface> {
            GroupApiInterfaceImpl()
        }
        single<ExpenseApiInterface> {
            ExpenseApiInterfaceImpl()
        }
        single<SettlementApiInterface> {
            SettlementApiInterfaceImpl()
        }
        single<InviteApiInterface> {
            InviteApiInterfaceImpl()
        }

        single<UserRepository> {
            UserRepositoryImpl(
                userApiInterface = get(),
                userQuery = get()
            )
        }
        single<GroupRepository> {
            GroupRepositoryImpl(
                groupApiInterface = get(),
                tripsQuery = get(),
                tripManagerQuery = get(),
                userQuery = get(),
                userApiInterface = get()
            )
        }
        single<ExpenseRepository> {
            ExpenseRepositoryImpl(
                expenseApiInterface = get(),
                expenseQuery = get(),
                expenseShareQuery = get(),
                userQuery = get(),
                userApiInterface = get()
            )
        }

        single<GetOrCreateUserUseCase> {
            GetOrCreateUserUseCase(repository = get())
        }
        single<CreateGroupUseCase> {
            CreateGroupUseCase(
                repository = get()
            )
        }
        single<GetAllUserUseCase> {
            GetAllUserUseCase(
                repository = get()
            )
        }
        single<GetAllGroupsUseCase> {
            GetAllGroupsUseCase(
                repository = get()
            )
        }
        single<GetGroupUseCase> {
            GetGroupUseCase(
                repository = get()
            )
        }
        single<UpdateGroupUseCase> {
            UpdateGroupUseCase(
                repository = get()
            )
        }
        single<DeleteGroupUseCase> {
            DeleteGroupUseCase(
                repository = get()
            )
        }
        single<GetGroupMembersUseCase> {
            GetGroupMembersUseCase(
                repository = get()
            )
        }
        single<GetAllExpenseByGroupIdUseCase> {
            GetAllExpenseByGroupIdUseCase(
                repository = get()
            )
        }
        single<CreateExpenseUseCase> {
            CreateExpenseUseCase(
                repository = get()
            )
        }
        single<AddMultipleMemberToGroupUseCase> {
            AddMultipleMemberToGroupUseCase(
                repository = get()
            )
        }
        single<UpdateExpenseUseCase> {
            UpdateExpenseUseCase(repository = get())
        }
        single<DeleteExpenseUseCase> {
            DeleteExpenseUseCase(repository = get())
        }
        single<UpdateUserUseCase> {
            UpdateUserUseCase(repository = get())
        }
        single<RemoveMembersFromGroupUseCase> {
            RemoveMembersFromGroupUseCase(repository = get())
        }
        single<CreateSettlementUseCase> {
            CreateSettlementUseCase(
                settlementApiInterface = get(),
                settlementQuery = get()
            )
        }
        single<GetGroupBalancesUseCase> {
            GetGroupBalancesUseCase(
                expenseQuery = get(),
                expenseShareQuery = get(),
                calculator = get()
            )
        }
        single<GetUserFriendsUseCase> {
            GetUserFriendsUseCase(userApiInterface = get())
        }
        single<GetGroupSettlementsUseCase> {
            GetGroupSettlementsUseCase(settlementQuery = get())
        }
        single<DeleteSettlementUseCase> {
            DeleteSettlementUseCase(
                settlementApiInterface = get(),
                settlementQuery        = get()
            )
        }
        single<CreateInviteLinkUseCase> {
            CreateInviteLinkUseCase(inviteApiInterface = get())
        }
        single<PreviewInviteLinkUseCase> {
            PreviewInviteLinkUseCase(inviteApiInterface = get())
        }
        single<AcceptInviteLinkUseCase> {
            AcceptInviteLinkUseCase(inviteApiInterface = get())
        }
        single<GetUserByIdUseCase> {
            GetUserByIdUseCase(userQuery = get())
        }
        single<GetExpenseByIdUseCase> {
            GetExpenseByIdUseCase(expenseQuery = get())
        }
        single<GetExpenseSharesByExpenseIdUseCase> {
            GetExpenseSharesByExpenseIdUseCase(expenseShareQuery = get())
        }
        single<GetUserByIdUseCase> {
            GetUserByIdUseCase(userQuery = get())
        }
        single<GetUserProfileUseCase> {
            GetUserProfileUseCase(userQuery = get())
        }
        single<GetDashboardSummaryUseCase> {
            GetDashboardSummaryUseCase(
                userQuery = get(),
                getAllGroupsUseCase = get(),
                getGroupMembersUseCase = get(),
                getAllExpenseByGroupIdUseCase = get(),
                getGroupBalancesUseCase = get(),
                getGroupSettlementsUseCase = get()
            )
        }
        single<RefreshGroupUseCase> {
            RefreshGroupUseCase(groupRepository = get())
        }

        single<android.content.SharedPreferences> {
            androidContext().getSharedPreferences(
                "SplitWellPrefs",
                android.content.Context.MODE_PRIVATE
            )
        }
        single { SettlementCalculator() }

        viewModel<GroupCreationViewModel> {
            GroupCreationViewModel(
                createGroupUseCase = get(),
                getAllUserUseCase = get()
            )
        }
        viewModel<GroupsDataViewModel> {
            GroupsDataViewModel(
                getAllGroupsUseCase = get(),
                getGroupMembersUseCase = get(),
                getAllExpenseByGroupIdUseCase = get()
            )
        }
        viewModel<GroupUpdatingViewModel> {
            GroupUpdatingViewModel(
                getGroupUseCase = get(),
                getGroupMembersUseCase = get(),
                updateGroupUseCase = get(),
                deleteGroupUseCase = get(),
                removeMembersFromGroupUseCase = get()
            )
        }
        viewModel<GroupDetailViewModel> {
            GroupDetailViewModel(
                getAllExpenseByGroupIdUseCase = get(),
                getGroupUseCase = get(),
                getGroupMembersUseCase = get(),
                createInviteLinkUseCase      = get(),
                refreshGroupUseCase          = get()
            )
        }
        viewModel<ExpenseViewModel> {
            ExpenseViewModel(
                getGroupMembersUseCase = get(),
                createExpenseUseCase = get()
            )
        }
        viewModel<AddMemberViewModel> {
            AddMemberViewModel(
                getAllUserUseCase = get(),
                getGroupMembersUseCase = get(),
                addMultipleMemberToGroupUseCase = get()
            )
        }
        viewModel<ExpenseDetailViewModel> {
            ExpenseDetailViewModel(
                getExpenseByIdUseCase             = get(),
                getExpenseSharesByExpenseIdUseCase = get(),
                deleteExpenseUseCase = get()
            )
        }
        viewModel<ExpenseUpdateViewModel> {
            ExpenseUpdateViewModel(
                getExpenseByIdUseCase             = get(),
                getExpenseSharesByExpenseIdUseCase = get(),
                getGroupMembersUseCase = get(),
                updateExpenseUseCase = get()
            )
        }
        viewModel<LoginViewModel> {
            LoginViewModel(
                getOrCreateUserUseCase = get(),
                sharedPreferences = get()
            )
        }
        viewModel<ProfileEditViewModel> {
            ProfileEditViewModel(
                getUserProfileUseCase = get(),
                updateUserUseCase = get()
            )
        }
        viewModel<DashboardViewModel> {
            DashboardViewModel(
                getDashboardSummaryUseCase = get()
            )
        }
        viewModel<FriendListViewModel> {
            FriendListViewModel(getUserFriendsUseCase = get())
        }
        viewModel<SettlementViewModel> {
            SettlementViewModel(
                getGroupBalancesUseCase = get(),
                createSettlementUseCase = get(),
                deleteSettlementUseCase = get(),
                getGroupMembersUseCase = get(),
                getGroupSettlementsUseCase = get(),
                getUserByIdUseCase         = get()
            )
        }
        viewModel<InvitePreviewViewModel> {
            InvitePreviewViewModel(
                previewInviteLinkUseCase = get(),
                acceptInviteLinkUseCase = get()
            )
        }

        single<SyncManager> {
            SyncManager(
                groupRepository = get(),
                expenseRepository = get(),
                sharedPreferences = get()
            )
        }

        single<AppLifecycleObserver> {
            AppLifecycleObserver(syncManager = get())
        }
    }
}