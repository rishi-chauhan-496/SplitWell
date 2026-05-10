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
import com.example.splitbuddy.domain.repository.GroupRepository

class GroupRepositoryImpl(
    val groupApiInterface: GroupApiInterface,
    val tripsQuery: TripsQuery,
    val tripManagerQuery: TripManagerQuery,
    val userQuery: UserQuery,
    val userApiInterface: UserApiInterface
): GroupRepository {

    override suspend fun groupCreation(request: CreateGroupRequest): Trip? {
        val data = groupApiInterface.groupCreationApi(request)

        tripsQuery.insertTrips(data)

        data.members?.forEach { member ->
            try {
                val user = userApiInterface.getUserById(member.userId)
                userQuery.insertUser(user)
                tripManagerQuery.insertTripManager(member)  // ← inside try
            } catch (_: Exception) { }
        }

        return tripsQuery.getTrips(data.id)
    }

    override suspend fun getAllGroups(userId: String): List<Trip> {
        val localData = tripsQuery.getAllTrips()

        try {
            val remoteData = groupApiInterface.getGroupsByUserId(userId)

            remoteData.forEach { group ->
                tripsQuery.insertTrips(group)

                group.members?.forEach { member ->
                    try {
                        val user = userApiInterface.getUserById(member.userId)
                        userQuery.insertUser(user)
                        tripManagerQuery.insertTripManager(member)  // ← inside try
                    } catch (_: Exception) { }
                }
            }
        } catch (_: Exception) { }

        return localData
    }

    override suspend fun getGroup(groupId: String): Trip? {

        return tripsQuery.getTrips(groupId)
    }

    override suspend fun updateGroup(
        groupId: String,
        request: UpdateGroupRequest
    ): Boolean {
        val data = groupApiInterface.updateGroup(groupId,request)

        return tripsQuery.updateTrips(data)
    }


    override suspend fun deleteGroup(groupId: String): Boolean {
        val data = groupApiInterface.deleteGroup(groupId)

        return tripsQuery.deleteTrips(data)
    }

    override suspend fun getGroupMemberByGroupId(groupId: String): List<TripManager> {

        return tripManagerQuery.getTripManagerByTripId(groupId)
    }

    override suspend fun updateGroupMemberByGroupId(groupId: String, userId: String): Boolean {
        val data = groupApiInterface.updateMembersToGroup(groupId, userId)

        return tripManagerQuery.updateTripManager(data)
    }

    override suspend fun addMultipleMemberToGroup(
        groupId: String,
        body: AddMembersRequest
    ): Boolean {
        val data = groupApiInterface.addMultipleMemberToGroup(groupId, body)

        return data.all { member ->
            try {
                val user = userApiInterface.getUserById(member.userId)
                userQuery.insertUser(user)
                tripManagerQuery.insertTripManager(member)  // ← inside try
                true
            } catch (_: Exception) {
                false
            }
        }
    }
}