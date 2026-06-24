package com.app.splitwell.data.repository

import com.app.splitwell.data.local.model.Trip
import com.app.splitwell.data.local.model.TripManager
import com.app.splitwell.data.local.query.TripManagerQuery
import com.app.splitwell.data.local.query.TripsQuery
import com.app.splitwell.data.local.query.UserQuery
import com.app.splitwell.data.remote.group.AddMembersRequest
import com.app.splitwell.data.remote.group.CreateGroupRequest
import com.app.splitwell.data.remote.group.GroupApiInterface
import com.app.splitwell.data.remote.group.RemoveMembersRequest
import com.app.splitwell.data.remote.group.UpdateGroupRequest
import com.app.splitwell.data.remote.user.UserApiInterface
import com.app.splitwell.data.remote.user.UserResponse
import com.app.splitwell.data.util.Resource
import com.app.splitwell.data.util.toAppError
import com.app.splitwell.domain.repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GroupRepositoryImpl(
    val groupApiInterface: GroupApiInterface,
    val tripsQuery: TripsQuery,
    val tripManagerQuery: TripManagerQuery,
    val userQuery: UserQuery,
    val userApiInterface: UserApiInterface
) : GroupRepository {

    // ── Observable Flow ───────────────────────────────────────────────────────

    private val _groupsFlow = MutableStateFlow<Resource<List<Trip>>>(
        Resource.Success(emptyList())
    )
    override val groupsFlow: StateFlow<Resource<List<Trip>>> = _groupsFlow

    // ── Sync — fetch from API, write to DB, emit updated list ─────────────────

    override suspend fun sync(userId: String) {
        try {
            val remoteData = groupApiInterface.getGroupsByUserId(userId)

            remoteData.forEach { group ->
                tripsQuery.insertTrips(group)

                val activeUserIds = mutableSetOf<String>()
                group.members?.forEach { member ->
                    try {
                        member.user.let { user ->
                            userQuery.insertUser(
                                UserResponse(
                                    id          = user.id,
                                    username    = user.username,
                                    firstName   = user.firstName,
                                    lastName    = user.lastName,
                                    contact     = user.contact,
                                    email       = user.email,
                                    socialMediaId = user.socialMediaId,
                                    isActive    = user.isActive,
                                    isDeleted   = user.isDeleted,
                                    createdAt   = user.createdAt,
                                    updatedAt   = user.updatedAt
                                )
                            )
                        }
                        tripManagerQuery.insertTripManager(member)
                        if (member.isActive) activeUserIds.add(member.userId)
                    } catch (_: Exception) { }
                }
                tripManagerQuery.markInactiveIfMissing(group.id, activeUserIds)
            }

            // Remove groups no longer returned — deleted, or you're no longer a member
            val freshGroupIds = remoteData.map { it.id }.toSet()
            tripsQuery.deleteMissing(freshGroupIds)

            // Emit fresh data — UI updates silently
            _groupsFlow.value = Resource.Success(tripsQuery.getAllTrips())

        } catch (e: Exception) {
            // Emit local data + error — UI shows stale data + offline banner
            _groupsFlow.value = Resource.Error(
                error = e.toAppError(),
                data  = tripsQuery.getAllTrips()
            )
        }
    }

    // ── Write operations ──────────────────────────────────────────────────────

    override suspend fun groupCreation(request: CreateGroupRequest): Resource<Trip> {
        return try {
            val data = groupApiInterface.groupCreationApi(request)
            tripsQuery.insertTrips(data)

            data.members?.forEach { member ->
                try {
                    member.user.let { user ->
                        userQuery.insertUser(
                            UserResponse(
                                id = user.id,
                                username = user.username,
                                firstName = user.firstName,
                                lastName = user.lastName,
                                contact = user.contact,
                                email = user.email,
                                socialMediaId = user.socialMediaId,
                                isActive = user.isActive,
                                isDeleted = user.isDeleted,
                                createdAt = user.createdAt,
                                updatedAt = user.updatedAt
                            )
                        )
                    }
                    tripManagerQuery.insertTripManager(member)
                } catch (_: Exception) { }
            }

            // Refresh flow after write
            _groupsFlow.value = Resource.Success(tripsQuery.getAllTrips())

            Resource.Success(tripsQuery.getTrips(data.id)!!)

        } catch (e: Exception) {
            Resource.Error(error = e.toAppError())
        }
    }

    override suspend fun updateGroup(
        groupId: String,
        request: UpdateGroupRequest
    ): Resource<Boolean> {
        return try {
            val data = groupApiInterface.updateGroup(groupId, request)
            tripsQuery.updateTrips(data)
            _groupsFlow.value = Resource.Success(tripsQuery.getAllTrips())
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(error = e.toAppError())
        }
    }

    override suspend fun deleteGroup(groupId: String): Resource<Boolean> {
        return try {
            val data = groupApiInterface.deleteGroup(groupId)
            tripsQuery.deleteTrips(data)
            _groupsFlow.value = Resource.Success(tripsQuery.getAllTrips())
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(error = e.toAppError())
        }
    }

    override suspend fun addMultipleMemberToGroup(
        groupId: String,
        body: AddMembersRequest
    ): Resource<Boolean> {
        return try {
            val data = groupApiInterface.addMultipleMemberToGroup(groupId, body)
            data.forEach { member ->
                member.user.let { user ->
                    userQuery.insertUser(
                        UserResponse(
                            id          = user.id,
                            username    = user.username,
                            firstName   = user.firstName,
                            lastName    = user.lastName,
                            contact     = user.contact,
                            email       = user.email,
                            socialMediaId = user.socialMediaId,
                            isActive    = user.isActive,
                            isDeleted   = user.isDeleted,
                            createdAt   = user.createdAt,
                            updatedAt   = user.updatedAt
                        )
                    )
                }
                tripManagerQuery.insertTripManager(member)
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(error = e.toAppError())
        }
    }

    override suspend fun removeMembersFromGroup(
        groupId: String,
        request: RemoveMembersRequest
    ): Resource<Boolean> {
        return try {
            val data = groupApiInterface.removeMembersFromGroup(groupId, request)
            data.forEach { tripManagerQuery.updateTripManager(it) }
            _groupsFlow.value = Resource.Success(tripsQuery.getAllTrips())
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(error = e.toAppError())
        }
    }

    override suspend fun getGroup(groupId: String): Trip? =
        tripsQuery.getTrips(groupId)

    override suspend fun getGroupMemberByGroupId(groupId: String): List<TripManager> =
        tripManagerQuery.getTripManagerByTripId(groupId)

    override suspend fun refreshGroup(groupId: String): Trip? {
        return try {
            val data = groupApiInterface.getGroupById(groupId)
            tripsQuery.insertTrips(data)

            val activeUserIds = mutableSetOf<String>()
            data.members?.forEach { member ->
                try {
                    member.user.let { user ->
                        userQuery.insertUser(
                            UserResponse(
                                id          = user.id,
                                username    = user.username,
                                firstName   = user.firstName,
                                lastName    = user.lastName,
                                contact     = user.contact,
                                email       = user.email,
                                socialMediaId = user.socialMediaId,
                                isActive    = user.isActive,
                                isDeleted   = user.isDeleted,
                                createdAt   = user.createdAt,
                                updatedAt   = user.updatedAt
                            )
                        )
                    }
                    tripManagerQuery.insertTripManager(member)
                    if (member.isActive) activeUserIds.add(member.userId)
                } catch (_: Exception) { }
            }
            tripManagerQuery.markInactiveIfMissing(groupId, activeUserIds)

            tripsQuery.getTrips(groupId)
        } catch (e: Exception) {
            // Network failed — fall back to whatever's already cached
            tripsQuery.getTrips(groupId)
        }
    }
}