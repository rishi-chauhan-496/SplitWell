package com.app.splitwell.data.remote.group

//Group creation body
data class CreateGroupRequest(
    val groupTitle: String,
    val managerUserId: String,
    val memberUserIds: List<String> = emptyList()
)

// Group response body Except when adding multiple member
data class GroupResponse(
    val id: String,
    val groupTitle: String,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val members: List<Member>?,
)
// response body when adding multiple member
data class Member(
    val id: String,
    val groupId: String,
    val userId: String,
    val role: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val joinedAt: String,
    val leftAt: String,
    val createdAt: String,
    val updatedAt: String,
    val user: User,
)

data class User(
    val id: String,
    val username: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val firstName: String,
    val lastName: String,
    val contact: String,
    val email: String,
    val socialMediaId: String,
    val createdAt: String,
    val updatedAt: String,
)

// Group Updating body
data class UpdateGroupRequest(
    val groupTitle: String
)

// Add multiple member body
data class AddMembersRequest(
    val members: List<MemberItem>
)
data class MemberItem(
    val userId: String,
    val role: String = "MEMBER" // default
)

data class RemoveMembersRequest(
    val userIds: List<String>
)
