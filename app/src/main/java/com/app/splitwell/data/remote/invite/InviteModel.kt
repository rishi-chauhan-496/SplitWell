package com.app.splitwell.data.remote.invite

import com.google.gson.annotations.SerializedName

data class CreateInviteLinkRequest(
    val createdByUserId: String,
    val expiresAt: String? = null       // optional expiry date
)

data class InviteLinkResponse(
    val id: String,
    val groupId: String,
    val token: String,
    val inviteUrl: String,
    val createdByUserId: String,
    val expiresAt: String?,
    val isActive: Boolean,
    val createdAt: String
)

data class InvitePreviewResponse(
    val id: String,
    val token: String,
    val inviteUrl: String,
    val isExpired: Boolean,
    val isActive: Boolean,
    val expiresAt: String?,
    val group: InviteGroupInfo,
    @SerializedName("createdByUser")
    val creator: InviteCreatorInfo
)

data class InviteGroupInfo(
    val id: String,
    val groupTitle: String,
    val memberCount: Int = 0   // server doesn't send this yet — see note below
)

data class InviteCreatorInfo(
    val id: String,
    val username: String,
    val firstName: String
)

data class AcceptInviteRequest(
    val userId: String
)

data class AcceptInviteResponse(
    val groupId: String,
    val userId: String,
    val role: String
)