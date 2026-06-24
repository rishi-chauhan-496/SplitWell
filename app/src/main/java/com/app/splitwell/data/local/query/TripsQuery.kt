package com.app.splitwell.data.local.query

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.app.splitwell.data.local.database.Database
import com.app.splitwell.data.local.database.Database.TripTable
import com.app.splitwell.data.local.model.Trip
import com.app.splitwell.data.remote.group.GroupResponse

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

    // DELETE — fully remove a group and everything that belongs to it,
    // right when the server confirms to delete.
    fun deleteTrips(trip: GroupResponse): Boolean {
        val db = dbHelper.writableDatabase
        cascadeDeleteTrip(db, trip.id)
        return true
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

        if (cursor.moveToFirst()) {
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

    // DELETE MISSING — remove groups no longer returned for this user
    // (the group was deleted, or you're no longer an active member)
    fun deleteMissing(freshGroupIds: Set<String>): List<String> {
        val db = dbHelper.writableDatabase

        val cursor = db.rawQuery(
            "SELECT ${TripTable.ID} FROM ${TripTable.TABLE_NAME}",
            null
        )

        val staleIds = mutableListOf<String>()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(TripTable.ID))
                if (id !in freshGroupIds) staleIds.add(id)
            } while (cursor.moveToNext())
        }
        cursor.close()

        staleIds.forEach { id -> cascadeDeleteTrip(db, id) }

        return staleIds
    }

    // Deletes everything that hangs off one group, then the group row itself.
    // Order matters — TripManager/ExpenseDemo/Settlement all have a FOREIGN KEY
    // on trips.id with no ON DELETE CASCADE, so children must go first.
    private fun cascadeDeleteTrip(db: SQLiteDatabase, tripId: String) {
        db.execSQL(
            """
        DELETE FROM ${Database.ExpenseShareTable.TABLE_NAME}
        WHERE ${Database.ExpenseShareTable.EXPENSE_ID} IN (
            SELECT ${Database.ExpenseTable.ID} FROM ${Database.ExpenseTable.TABLE_NAME}
            WHERE ${Database.ExpenseTable.TRIP_ID} = ?
        )
        """.trimIndent(),
            arrayOf(tripId)
        )
        db.delete(
            Database.ExpenseTable.TABLE_NAME,
            "${Database.ExpenseTable.TRIP_ID} = ?",
            arrayOf(tripId)
        )
        db.delete(
            Database.SettlementTable.TABLE_NAME,
            "${Database.SettlementTable.TRIP_ID} = ?",
            arrayOf(tripId)
        )
        db.delete(
            Database.TripManagerTable.TABLE_NAME,
            "${Database.TripManagerTable.TRIP_ID} = ?",
            arrayOf(tripId)
        )
        db.delete(TripTable.TABLE_NAME, "${TripTable.ID} = ?", arrayOf(tripId))
    }
}