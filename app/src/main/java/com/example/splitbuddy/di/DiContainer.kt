package com.example.splitbuddy.di

import com.example.splitbuddy.data.remote.group.GroupApiInterfaceImpl
import com.example.splitbuddy.data.remote.group.GroupApiInterface
import com.example.splitbuddy.domain.usecase.group.CreateGroupUseCase
import com.example.splitbuddy.ui.MainApplication
import com.example.splitbuddy.data.local.database.Database
import com.example.splitbuddy.data.local.query.ExpenseQuery
import com.example.splitbuddy.data.local.query.ExpenseShareQuery
import com.example.splitbuddy.data.local.query.TripManagerQuery
import com.example.splitbuddy.data.local.query.TripsQuery
import com.example.splitbuddy.data.local.query.UserQuery
import com.example.splitbuddy.data.remote.expense.ExpenseApiInterface
import com.example.splitbuddy.data.remote.expense.ExpenseApiInterfaceImpl
import com.example.splitbuddy.data.remote.user.UserApiInterface
import com.example.splitbuddy.data.remote.user.UserApiInterfaceImpl
import com.example.splitbuddy.data.repository.ExpenseRepositoryImpl
import com.example.splitbuddy.data.repository.GroupRepositoryImpl
import com.example.splitbuddy.data.repository.UserRepositoryImpl
import com.example.splitbuddy.data.sync.AppLifecycleObserver
import com.example.splitbuddy.data.sync.SyncManager
import com.example.splitbuddy.domain.repository.ExpenseRepository
import com.example.splitbuddy.domain.repository.GroupRepository
import com.example.splitbuddy.domain.repository.UserRepository
import com.example.splitbuddy.domain.usecase.expense.CreateExpenseUseCase
import com.example.splitbuddy.domain.usecase.expense.DeleteExpenseUseCase
import com.example.splitbuddy.domain.usecase.expense.GetAllExpenseByGroupIdUseCase
import com.example.splitbuddy.domain.usecase.expense.UpdateExpenseUseCase
import com.example.splitbuddy.domain.usecase.group.DeleteGroupUseCase
import com.example.splitbuddy.domain.usecase.group.GetAllGroupsUseCase
import com.example.splitbuddy.domain.usecase.group.GetGroupMembersUseCase
import com.example.splitbuddy.domain.usecase.group.GetGroupUseCase
import com.example.splitbuddy.domain.usecase.group.UpdateGroupUseCase
import com.example.splitbuddy.domain.usecase.group.AddMultipleMemberToGroupUseCase
import com.example.splitbuddy.domain.usecase.group.RemoveMembersFromGroupUseCase
import com.example.splitbuddy.domain.usecase.user.GetAllUserUseCase
import com.example.splitbuddy.domain.usecase.user.GetOrCreateUserUseCase
import com.example.splitbuddy.domain.usecase.user.UpdateUserUseCase
import com.example.splitbuddy.ui.home_screen.dashboard.DashboardViewModel
import com.example.splitbuddy.ui.home_screen.expense.expense_creating.ExpenseViewModel
import com.example.splitbuddy.ui.home_screen.expense.expense_screen.ExpenseDetailViewModel
import com.example.splitbuddy.ui.home_screen.expense.expense_update_screen.ExpenseUpdateViewModel
import com.example.splitbuddy.ui.home_screen.friend.FriendListViewModel
import com.example.splitbuddy.ui.home_screen.group.group_add_member_screen.AddMemberViewModel
import com.example.splitbuddy.ui.home_screen.group.group_screen.GroupDetailViewModel
import com.example.splitbuddy.ui.home_screen.group.group_updating.GroupUpdatingViewModel
import com.example.splitbuddy.ui.home_screen.group.group_creation.GroupCreationViewModel
import com.example.splitbuddy.ui.home_screen.group.groups_screen.GroupsDataViewModel
import com.example.splitbuddy.ui.home_screen.top_bar.TopBarViewModel
import com.example.splitbuddy.ui.login_screen.LoginViewModel
import com.example.splitbuddy.ui.profile.ProfileEditViewModel
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

        single<UserApiInterface> {
            UserApiInterfaceImpl()
        }

        single<GroupApiInterface> {
            GroupApiInterfaceImpl()
        }
        single<ExpenseApiInterface> {
            ExpenseApiInterfaceImpl()
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
                tripsQuery        = get(),
                tripManagerQuery  = get(),
                userQuery         = get(),
                userApiInterface  = get()
            )
        }
        single<ExpenseRepository> {
            ExpenseRepositoryImpl(
                expenseApiInterface = get(),
                expenseQuery        = get(),
                expenseShareQuery   = get(),
                userQuery           = get(),
                userApiInterface    = get()
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

        single<android.content.SharedPreferences> {
            androidContext().getSharedPreferences("SplitBuddyPrefs", android.content.Context.MODE_PRIVATE)
        }

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
                getGroupUseCase              = get(),
                getGroupMembersUseCase       = get(),
                updateGroupUseCase           = get(),
                deleteGroupUseCase           = get(),
                removeMembersFromGroupUseCase = get()
            )
        }
        viewModel<GroupDetailViewModel> {
            GroupDetailViewModel(
                getAllExpenseByGroupIdUseCase = get(),
                getGroupUseCase = get(),
                getGroupMembersUseCase = get()
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
                expenseQuery = get(),
                expenseShareQuery = get(),
                deleteExpenseUseCase = get()
            )
        }
        viewModel<ExpenseUpdateViewModel> {
            ExpenseUpdateViewModel(
                expenseQuery           = get(),
                expenseShareQuery      = get(),
                getGroupMembersUseCase = get(),
                updateExpenseUseCase   = get()
            )
        }
        viewModel<LoginViewModel> {
            LoginViewModel(
                getOrCreateUserUseCase = get(),
                sharedPreferences      = get()
            )
        }
        viewModel<ProfileEditViewModel> {
            ProfileEditViewModel(
                userQuery         = get(),
                updateUserUseCase = get()
            )
        }
        viewModel<DashboardViewModel> {
            DashboardViewModel(
                userQuery                    = get(),
                getAllGroupsUseCase           = get(),
                getGroupMembersUseCase       = get(),
                getAllExpenseByGroupIdUseCase = get()
            )
        }
        viewModel<FriendListViewModel> {
            FriendListViewModel(userQuery = get())
        }

        viewModel<TopBarViewModel> {
            TopBarViewModel()
        }

        single<SyncManager> {
            SyncManager(
                groupRepository   = get(),
                expenseRepository = get(),
                sharedPreferences = get()
            )
        }

        single<AppLifecycleObserver> {
            AppLifecycleObserver(syncManager = get())
        }
    }
}