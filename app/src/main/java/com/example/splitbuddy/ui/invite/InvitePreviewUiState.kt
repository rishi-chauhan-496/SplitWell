package com.example.splitbuddy.ui.invite

data class InvitePreviewUiState(
    val isLoading: Boolean = false,
    val isAccepting: Boolean = false,

    // Group info from preview API
    val groupName: String = "",
    val memberCount: Int = 0,
    val createdByName: String = "",
    val expiresAt: String? = null,
    val isExpired: Boolean = false,

    // Set after successful accept → triggers navigation to GroupScreen
    val joinedGroupId: String? = null,

    val error: String? = null
)