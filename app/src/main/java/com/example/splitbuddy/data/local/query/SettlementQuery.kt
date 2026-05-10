package com.example.splitbuddy.data.local.query

import android.content.ContentValues
import com.example.splitbuddy.data.local.database.Database
import com.example.splitbuddy.data.local.database.Database.SettlementTable
import com.example.splitbuddy.data.local.model.Settlement

class SettlementQuery(private val dbHelper: Database) {

    // INSERT
    fun insertSettlement(data: Settlement): Boolean {

        val db = dbHelper.writableDatabase
        val cv = ContentValues()

        cv.put(SettlementTable.ID, data.id)
        cv.put(SettlementTable.TRIP_ID, data.tripId)
        cv.put(SettlementTable.FROM_USER_ID, data.fromUserId)
        cv.put(SettlementTable.TO_USER_ID, data.toUserId)
        cv.put(SettlementTable.SETTLEMENT_AMT, data.settlementAmt)
        cv.put(SettlementTable.NOTE, data.note)
        cv.put(SettlementTable.CREATED_AT, data.createdAt)
        cv.put(SettlementTable.UPDATED_AT, data.updatedAt)

        return db.insert(SettlementTable.TABLE_NAME, null, cv) > 0
    }

    // UPDATE
    fun updateSettlement(data: Settlement): Boolean {

        val db = dbHelper.writableDatabase
        val cv = ContentValues()

        cv.put(SettlementTable.FROM_USER_ID, data.fromUserId)
        cv.put(SettlementTable.TO_USER_ID, data.toUserId)
        cv.put(SettlementTable.SETTLEMENT_AMT, data.settlementAmt)
        cv.put(SettlementTable.NOTE, data.note)
        cv.put(SettlementTable.UPDATED_AT, data.updatedAt)

        return db.update(
            SettlementTable.TABLE_NAME,
            cv,
            "${SettlementTable.ID} = ?",
            arrayOf(data.id)
        ) > 0
    }

    // SELECT BY TRIP
    fun getSettlementByTrip(tripId: String): List<Settlement> {

        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM ${SettlementTable.TABLE_NAME} WHERE ${SettlementTable.TRIP_ID} = ?",
            arrayOf(tripId)
        )

        val list = mutableListOf<Settlement>()

        while (cursor.moveToNext()) {
            list.add(
                Settlement(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(SettlementTable.ID)),
                    tripId = cursor.getString(cursor.getColumnIndexOrThrow(SettlementTable.TRIP_ID)),
                    fromUserId = cursor.getString(cursor.getColumnIndexOrThrow(SettlementTable.FROM_USER_ID)),
                    toUserId = cursor.getString(cursor.getColumnIndexOrThrow(SettlementTable.TO_USER_ID)),
                    settlementAmt = cursor.getDouble(cursor.getColumnIndexOrThrow(SettlementTable.SETTLEMENT_AMT)),
                    note = cursor.getString(cursor.getColumnIndexOrThrow(SettlementTable.NOTE)),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow(SettlementTable.CREATED_AT)),
                    updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(SettlementTable.UPDATED_AT)),
                    isDeleted = cursor.getInt(
                        cursor.getColumnIndexOrThrow(SettlementTable.IS_DELETED)
                    ) == 1
                )
            )
        }

        cursor.close()
        return list
    }

    fun getSettlementByUser(userId: String): List<Settlement> {

        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM ${SettlementTable.TABLE_NAME} WHERE ${SettlementTable.FROM_USER_ID} = ?",
            arrayOf(userId)
        )

        val list = mutableListOf<Settlement>()

        while (cursor.moveToNext()) {
            list.add(
                Settlement(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(SettlementTable.ID)),
                    tripId = cursor.getString(cursor.getColumnIndexOrThrow(SettlementTable.TRIP_ID)),
                    fromUserId = cursor.getString(cursor.getColumnIndexOrThrow(SettlementTable.FROM_USER_ID)),
                    toUserId = cursor.getString(cursor.getColumnIndexOrThrow(SettlementTable.TO_USER_ID)),
                    settlementAmt = cursor.getDouble(cursor.getColumnIndexOrThrow(SettlementTable.SETTLEMENT_AMT)),
                    note = cursor.getString(cursor.getColumnIndexOrThrow(SettlementTable.NOTE)),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow(SettlementTable.CREATED_AT)),
                    updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(SettlementTable.UPDATED_AT)),
                    isDeleted = cursor.getInt(
                        cursor.getColumnIndexOrThrow(SettlementTable.IS_DELETED)
                    ) == 1
                )
            )
        }

        cursor.close()
        return list
    }

    fun getSettlementByTripAndUser(tripId: String, userId: String): List<Settlement> {

        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM ${SettlementTable.TABLE_NAME} WHERE ${SettlementTable.TRIP_ID} = ? AND ${SettlementTable.FROM_USER_ID} = ?",
            arrayOf(tripId,userId)
        )

        val list = mutableListOf<Settlement>()

        while (cursor.moveToNext()) {
            list.add(
                Settlement(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(SettlementTable.ID)),
                    tripId = cursor.getString(cursor.getColumnIndexOrThrow(SettlementTable.TRIP_ID)),
                    fromUserId = cursor.getString(cursor.getColumnIndexOrThrow(SettlementTable.FROM_USER_ID)),
                    toUserId = cursor.getString(cursor.getColumnIndexOrThrow(SettlementTable.TO_USER_ID)),
                    settlementAmt = cursor.getDouble(cursor.getColumnIndexOrThrow(SettlementTable.SETTLEMENT_AMT)),
                    note = cursor.getString(cursor.getColumnIndexOrThrow(SettlementTable.NOTE)),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow(SettlementTable.CREATED_AT)),
                    updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(SettlementTable.UPDATED_AT)),
                    isDeleted = cursor.getInt(
                        cursor.getColumnIndexOrThrow(SettlementTable.IS_DELETED)
                    ) == 1
                )
            )
        }

        cursor.close()
        return list
    }
}