package com.app.splitwell.ui.home_screen.bottom_bar

import com.app.splitwell.R

sealed class BottomNavItem(
    val route: String,
    val icon: Int )
{
    object Dashboard: BottomNavItem("dashboard", R.drawable.home)

    object Groups: BottomNavItem("groups", R.drawable.group)

    object FriendList: BottomNavItem("friendList", R.drawable.friend_list)

    object Profile: BottomNavItem("profile", R.drawable.profile)

}