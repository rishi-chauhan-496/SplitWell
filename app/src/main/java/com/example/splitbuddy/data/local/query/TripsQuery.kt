package com.example.splitbuddy.data.local.query

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.splitbuddy.data.local.database.Database
import com.example.splitbuddy.data.local.database.Database.TripTable
import com.example.splitbuddy.data.local.model.Trip
import com.example.splitbuddy.data.remote.group.GroupResponse

class TripsQuery(private val dbHelper: Database) {

    // INSERT
    fun insertTrips(trip: GroupResponse): Boolean {

        val db = dbHelper.writableDatabase
        val cv = ContentValues()

        cv.put(TripTable.ID, trip.id)
        cv.put(TripTable.TRIP_TITLE, trip.groupTitle)
        cv.put(TripTable.CREATED_AT, trip.createdAt)
        cv.put(TripTable.UPDATED_AT, trip.updatedAt)

        return db.insertWithOnConflict(
            TripTable.TABLE_NAME, null, cv,
            SQLiteDatabase.CONFLICT_REPLACE
        ) > 0
    }

    // UPDATE
    fun updateTrips(trip: GroupResponse): Boolean {

        val db = dbHelper.writableDatabase
        val cv = ContentValues()

        cv.put(TripTable.TRIP_TITLE, trip.groupTitle)
        cv.put(TripTable.UPDATED_AT, trip.updatedAt)

        return db.update(
            TripTable.TABLE_NAME,
            cv,
            "${TripTable.ID} = ?",
            arrayOf(trip.id)
        ) > 0
    }

    //DELETE
    fun deleteTrips(trip: GroupResponse): Boolean {

        val db = dbHelper.writableDatabase
        val cv = ContentValues()

        cv.put(TripTable.IS_DELETED, trip.isDeleted)

        return db.update(
            TripTable.TABLE_NAME,
            cv,
            "${TripTable.ID} = ?",
            arrayOf(trip.id)
        ) > 0
    }

    // SELECT SINGLE TRIPS
    fun getTrips(tripId: String): Trip? {

        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM ${TripTable.TABLE_NAME} WHERE ${TripTable.ID} = ?",
            arrayOf(tripId)
        )

        var trip: Trip? = null

        if (cursor.moveToFirst()) {
            trip = Trip(
                id = cursor.getString(cursor.getColumnIndexOrThrow(TripTable.ID)),
                tripTitle = cursor.getString(cursor.getColumnIndexOrThrow(TripTable.TRIP_TITLE)),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow(TripTable.CREATED_AT)),
                updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(TripTable.UPDATED_AT)),
                isDeleted = cursor.getInt(
                    cursor.getColumnIndexOrThrow(TripTable.IS_DELETED)
                ) == 1
            )
        }

        cursor.close()
        return trip
    }

    fun getAllTrips(): List<Trip> {

        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM ${TripTable.TABLE_NAME}",
            null
        )

        val trip = mutableListOf<Trip>()

        if(cursor.moveToFirst()) {
             do {
                trip.add(
                    Trip(
                        id = cursor.getString(cursor.getColumnIndexOrThrow(TripTable.ID)),
                        tripTitle = cursor.getString(cursor.getColumnIndexOrThrow(TripTable.TRIP_TITLE)),
                        createdAt = cursor.getString(cursor.getColumnIndexOrThrow(TripTable.CREATED_AT)),
                        updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(TripTable.UPDATED_AT)),
                        isDeleted = cursor.getInt(
                            cursor.getColumnIndexOrThrow(TripTable.IS_DELETED)
                        ) == 1
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return trip
    }
}