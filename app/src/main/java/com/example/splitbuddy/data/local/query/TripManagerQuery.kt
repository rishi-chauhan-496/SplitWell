package com.example.splitbuddy.data.local.query

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.splitbuddy.data.local.database.Database
import com.example.splitbuddy.data.local.database.Database.TripManagerTable
import com.example.splitbuddy.data.local.database.Database.UserTable
import com.example.splitbuddy.data.local.model.TripManager
import com.example.splitbuddy.data.remote.group.Member

class TripManagerQuery(private val dbHelper: Database) {

    // ---------------- INSERT ----------------
    fun insertTripManager(manager: Member): Boolean {
        val db = dbHelper.writableDatabase
        val cv = ContentValues()

        cv.put(TripManagerTable.ID, manager.id)
        cv.put(TripManagerTable.TRIP_ID, manager.groupId)
        cv.put(TripManagerTable.USER_ID, manager.userId)
        cv.put(TripManagerTable.ROLE, manager.role)
        cv.put(TripManagerTable.JOINED_AT, manager.joinedAt)
        cv.put(TripManagerTable.LEFT_AT, manager.leftAt)
        cv.put(TripManagerTable.CREATED_AT, manager.createdAt)
        cv.put(TripManagerTable.UPDATED_AT, manager.updatedAt)

        return db.insertWithOnConflict(
            TripManagerTable.TABLE_NAME, null, cv,
            SQLiteDatabase.CONFLICT_REPLACE
        ) > 0
    }

    // ---------------- UPDATE ----------------
    fun updateTripManager(manager: Member): Boolean {
        val db = dbHelper.writableDatabase
        val cv = ContentValues()

        cv.put(TripManagerTable.IS_ACTIVE, manager.isActive)
        cv.put(TripManagerTable.IS_DELETED, manager.isDeleted)
        cv.put(TripManagerTable.ROLE, manager.role)
        cv.put(TripManagerTable.UPDATED_AT, manager.updatedAt)

        return db.update(
            TripManagerTable.TABLE_NAME, cv,
            "${TripManagerTable.ID} = ?",
            arrayOf(manager.id)
        ) > 0
    }

    // ---------------- SELECT SINGLE ----------------
    fun getTripManagerByUserIdAndTripId(userId: String, tripId: String): TripManager? {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM ${TripManagerTable.TABLE_NAME} WHERE ${TripManagerTable.USER_ID} = ? AND ${TripManagerTable.TRIP_ID} = ?",
            arrayOf(userId, tripId)
        )

        var manager: TripManager? = null

        if (cursor.moveToFirst()) {
            manager = TripManager(
                id = cursor.getString(cursor.getColumnIndexOrThrow(TripManagerTable.ID)),
                tripId = cursor.getString(cursor.getColumnIndexOrThrow(TripManagerTable.TRIP_ID)),
                userId = cursor.getString(cursor.getColumnIndexOrThrow(TripManagerTable.USER_ID)),
                role = cursor.getString(cursor.getColumnIndexOrThrow(TripManagerTable.ROLE)),
                isActive = cursor.getInt(cursor.getColumnIndexOrThrow(TripManagerTable.IS_ACTIVE)) == 1,
                joinedAt = cursor.getString(cursor.getColumnIndexOrThrow(TripManagerTable.JOINED_AT)),
                leftAt = cursor.getString(cursor.getColumnIndexOrThrow(TripManagerTable.LEFT_AT)),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow(TripManagerTable.CREATED_AT)),
                updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(TripManagerTable.UPDATED_AT)),
                isDeleted = cursor.getInt(cursor.getColumnIndexOrThrow(TripManagerTable.IS_DELETED)) == 1
            )
        }

        cursor.close()
        return manager
    }

    fun getTripManagerByUserId(userId: String): List<TripManager> {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM ${TripManagerTable.TABLE_NAME} WHERE ${TripManagerTable.USER_ID} = ?",
            arrayOf(userId)
        )

        val manager = mutableListOf<TripManager>()

        if (cursor.moveToFirst()) {
            do {
                manager.add(
                    TripManager(
                        id = cursor.getString(cursor.getColumnIndexOrThrow(TripManagerTable.ID)),
                        tripId = cursor.getString(cursor.getColumnIndexOrThrow(TripManagerTable.TRIP_ID)),
                        userId = cursor.getString(cursor.getColumnIndexOrThrow(TripManagerTable.USER_ID)),
                        role = cursor.getString(cursor.getColumnIndexOrThrow(TripManagerTable.ROLE)),
                        isActive = cursor.getInt(cursor.getColumnIndexOrThrow(TripManagerTable.IS_ACTIVE)) == 1,
                        joinedAt = cursor.getString(cursor.getColumnIndexOrThrow(TripManagerTable.JOINED_AT)),
                        leftAt = cursor.getString(cursor.getColumnIndexOrThrow(TripManagerTable.LEFT_AT)),
                        createdAt = cursor.getString(cursor.getColumnIndexOrThrow(TripManagerTable.CREATED_AT)),
                        updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(TripManagerTable.UPDATED_AT)),
                        isDeleted = cursor.getInt(cursor.getColumnIndexOrThrow(TripManagerTable.IS_DELETED)) == 1
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return manager
    }

    // ---------------- SELECT WITH JOIN (includes userName) ----------------

    fun getTripManagerByTripId(tripId: String): List<TripManager> {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT 
                tm.${TripManagerTable.ID},
                tm.${TripManagerTable.TRIP_ID},
                tm.${TripManagerTable.USER_ID},
                tm.${TripManagerTable.ROLE},
                tm.${TripManagerTable.IS_ACTIVE},
                tm.${TripManagerTable.JOINED_AT},
                tm.${TripManagerTable.LEFT_AT},
                tm.${TripManagerTable.CREATED_AT},
                tm.${TripManagerTable.UPDATED_AT},
                tm.${TripManagerTable.IS_DELETED},
                u.${UserTable.FIRST_NAME} AS u_first_name,
                u.${UserTable.LAST_NAME}  AS u_last_name
            FROM ${TripManagerTable.TABLE_NAME} tm
            INNER JOIN ${UserTable.TABLE_NAME} u
                ON tm.${TripManagerTable.USER_ID} = u.${UserTable.ID}
            WHERE tm.${TripManagerTable.TRIP_ID} = ?
            AND tm.${TripManagerTable.IS_DELETED} = 0
            AND tm.${TripManagerTable.IS_ACTIVE} = 1
            """.trimIndent(),
            arrayOf(tripId)
        )

        val managers = mutableListOf<TripManager>()

        if (cursor.moveToFirst()) {
            do {
                val firstName = cursor.getString(cursor.getColumnIndexOrThrow("u_first_name")) ?: ""
                val lastName  = cursor.getString(cursor.getColumnIndexOrThrow("u_last_name"))  ?: ""

                managers.add(
                    TripManager(
                        id       = cursor.getString(cursor.getColumnIndexOrThrow(TripManagerTable.ID)),
                        tripId   = cursor.getString(cursor.getColumnIndexOrThrow(TripManagerTable.TRIP_ID)),
                        userId   = cursor.getString(cursor.getColumnIndexOrThrow(TripManagerTable.USER_ID)),
                        userName = "$firstName $lastName".trim(),
                        role     = cursor.getString(cursor.getColumnIndexOrThrow(TripManagerTable.ROLE)),
                        isActive = cursor.getInt(cursor.getColumnIndexOrThrow(TripManagerTable.IS_ACTIVE)) == 1,
                        joinedAt = cursor.getString(cursor.getColumnIndexOrThrow(TripManagerTable.JOINED_AT)),
                        leftAt   = cursor.getString(cursor.getColumnIndexOrThrow(TripManagerTable.LEFT_AT)),
                        createdAt = cursor.getString(cursor.getColumnIndexOrThrow(TripManagerTable.CREATED_AT)),
                        updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(TripManagerTable.UPDATED_AT)),
                        isDeleted = cursor.getInt(cursor.getColumnIndexOrThrow(TripManagerTable.IS_DELETED)) == 1
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return managers
    }
}