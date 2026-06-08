package com.example.splitbuddy.data.repository

import com.example.splitbuddy.data.local.model.Trip
import com.example.splitbuddy.data.local.model.TripManager
import com.example.splitbuddy.data.local.query.TripManagerQuery
import com.example.splitbuddy.data.local.query.TripsQuery
import com.example.splitbuddy.data.local.query.UserQuery
import com.example.splitbuddy.data.remote.group.AddMembersRequest
import com.example.splitbuddy.data.remote.group.CreateGroupRequest
import com.example.splitbuddy.data.remote.group.GroupApiInterface
import com.example.splitbuddy.data.remote.group.UpdateGroupRequest
import com.example.splitbuddy.data.remote.user.UserApiInterface
import com.example.splitbuddy.data.util.AppError
import com.example.splitbuddy.data.util.Resource
import com.example.splitbuddy.data.util.toAppError
import com.example.splitbuddy.domain.repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

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
                group.members?.forEach { member ->
                    try {
                        syncUserIfNeeded(member.userId)
                        tripManagerQuery.insertTripManager(member)
                    } catch (_: Exception) { }
                }
            }

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
                    syncUserIfNeeded(member.userId)
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
                syncUserIfNeeded(member.userId)
                tripManagerQuery.insertTripManager(member)
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(error = e.toAppError())
        }
    }

    override suspend fun updateGroupMemberByGroupId(
        groupId: String,
        userId: String
    ): Resource<Boolean> {
        return try {
            val data = groupApiInterface.updateMembersToGroup(groupId, userId)
            tripManagerQuery.updateTripManager(data)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(error = e.toAppError())
        }
    }

    override suspend fun getGroup(groupId: String): Trip? =
        tripsQuery.getTrips(groupId)

    override suspend fun getGroupMemberByGroupId(groupId: String): List<TripManager> =
        tripManagerQuery.getTripManagerByTripId(groupId)

    // ── Private ───────────────────────────────────────────────────────────────

    private suspend fun syncUserIfNeeded(userId: String) {
        try {
            if (userQuery.getUser(userId) == null) {
                val user = userApiInterface.getUserById(userId)
                userQuery.insertUser(user)
            }
        } catch (_: Exception) { }
    }
}