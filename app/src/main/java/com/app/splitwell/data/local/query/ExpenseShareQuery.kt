package com.app.splitwell.data.local.query

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.app.splitwell.data.local.database.Database
import com.app.splitwell.data.local.database.Database.ExpenseShareTable
import com.app.splitwell.data.local.database.Database.UserTable
import com.app.splitwell.data.local.database.Database.ExpenseTable
import com.app.splitwell.data.local.model.ExpenseShare
import com.app.splitwell.data.remote.expense.Share

class ExpenseShareQuery(private val dbHelper: Database) {

    // INSERT
    fun insertExpenseShare(data: Share): Boolean {

        val db = dbHelper.writableDatabase
        val cv = ContentValues()

        cv.put(ExpenseShareTable.ID, data.id)
        cv.put(ExpenseShareTable.EXPENSE_ID, data.expenseId)
        cv.put(ExpenseShareTable.USER_ID, data.userId)
        cv.put(ExpenseShareTable.SHARED_AMOUNT, data.shareAmount)
        cv.put(ExpenseShareTable.SHARED_PERCENT, data.sharePercent)
        cv.put(ExpenseShareTable.CREATED_AT, data.createdAt)
        cv.put(ExpenseShareTable.UPDATED_AT, data.updatedAt)

        return db.insertWithOnConflict(
            ExpenseShareTable.TABLE_NAME,
            null,
            cv,
            SQLiteDatabase.CONFLICT_REPLACE
        ) > 0
    }

    // UPDATE
    fun updateLedger(data: ExpenseShare): Boolean {

        val db = dbHelper.writableDatabase
        val cv = ContentValues()

        cv.put(ExpenseShareTable.SHARED_AMOUNT, data.sharedAmount)
        cv.put(ExpenseShareTable.SHARED_PERCENT, data.sharedPercent)
        cv.put(ExpenseShareTable.UPDATED_AT, data.updatedAt)

        return db.update(
            ExpenseShareTable.TABLE_NAME,
            cv,
            "${ExpenseShareTable.ID} = ?",
            arrayOf(data.id)
        ) > 0
    }

    // SELECT SINGLE
    fun getLedger(id: String): ExpenseShare? {

        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM ${ExpenseShareTable.TABLE_NAME} WHERE ${ExpenseShareTable.ID} = ?",
            arrayOf(id)
        )

        var ledger: ExpenseShare? = null

        if (cursor.moveToFirst()) {
            ledger = ExpenseShare(
                id = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseShareTable.ID)),
                expenseId = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseShareTable.EXPENSE_ID)),
                userId = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseShareTable.USER_ID)),
                sharedAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(ExpenseShareTable.SHARED_AMOUNT)),
                sharedPercent = cursor.getDouble(cursor.getColumnIndexOrThrow(ExpenseShareTable.SHARED_PERCENT)),
                isIncluded = cursor.getInt(
                    cursor.getColumnIndexOrThrow(ExpenseShareTable.IS_INCLUDED)
                ) == 1,
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseShareTable.CREATED_AT)),
                updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseShareTable.UPDATED_AT)),
                isDeleted = cursor.getInt(
                    cursor.getColumnIndexOrThrow(ExpenseShareTable.IS_DELETED)
                ) == 1
            )
        }

        cursor.close()
        return ledger
    }

    fun getSharesByExpenseId(expenseId: String): List<ExpenseShare> {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
        SELECT
            es.${ExpenseShareTable.ID},
            es.${ExpenseShareTable.EXPENSE_ID},
            es.${ExpenseShareTable.USER_ID},
            es.${ExpenseShareTable.SHARED_AMOUNT},
            es.${ExpenseShareTable.SHARED_PERCENT},
            es.${ExpenseShareTable.IS_INCLUDED},
            es.${ExpenseShareTable.CREATED_AT},
            es.${ExpenseShareTable.UPDATED_AT},
            es.${ExpenseShareTable.IS_DELETED},
            u.${UserTable.FIRST_NAME} AS u_first_name,
            u.${UserTable.LAST_NAME}  AS u_last_name
        FROM ${ExpenseShareTable.TABLE_NAME} es
        INNER JOIN ${UserTable.TABLE_NAME} u
            ON es.${ExpenseShareTable.USER_ID} = u.${UserTable.ID}
        WHERE es.${ExpenseShareTable.EXPENSE_ID} = ?
        AND es.${ExpenseShareTable.IS_DELETED} = 0
        """.trimIndent(),
            arrayOf(expenseId)
        )

        val shares = mutableListOf<ExpenseShare>()

        if (cursor.moveToFirst()) {
            do {
                val firstName = cursor.getString(cursor.getColumnIndexOrThrow("u_first_name")) ?: ""
                val lastName  = cursor.getString(cursor.getColumnIndexOrThrow("u_last_name"))  ?: ""

                shares.add(
                    ExpenseShare(
                        id           = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseShareTable.ID)),
                        expenseId    = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseShareTable.EXPENSE_ID)),
                        userId       = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseShareTable.USER_ID)),
                        userName     = "$firstName $lastName".trim(),
                        sharedAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(ExpenseShareTable.SHARED_AMOUNT)),
                        sharedPercent = cursor.getDouble(cursor.getColumnIndexOrThrow(ExpenseShareTable.SHARED_PERCENT)),
                        isIncluded   = cursor.getInt(cursor.getColumnIndexOrThrow(ExpenseShareTable.IS_INCLUDED)) == 1,
                        createdAt    = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseShareTable.CREATED_AT)),
                        updatedAt    = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseShareTable.UPDATED_AT)),
                        isDeleted    = cursor.getInt(cursor.getColumnIndexOrThrow(ExpenseShareTable.IS_DELETED)) == 1
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return shares
    }

    // Gets ALL shares for a group in ONE query
    // Uses JOIN with ExpenseDemo to filter by trip_id
    fun getSharesByGroupId(groupId: String): List<ExpenseShare> {

        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
        SELECT
            es.${ExpenseShareTable.ID},
            es.${ExpenseShareTable.EXPENSE_ID},
            es.${ExpenseShareTable.USER_ID},
            es.${ExpenseShareTable.SHARED_AMOUNT},
            es.${ExpenseShareTable.SHARED_PERCENT},
            es.${ExpenseShareTable.IS_INCLUDED},
            es.${ExpenseShareTable.CREATED_AT},
            es.${ExpenseShareTable.UPDATED_AT},
            es.${ExpenseShareTable.IS_DELETED},
            u.${UserTable.FIRST_NAME} AS u_first_name,
            u.${UserTable.LAST_NAME}  AS u_last_name
        FROM ${ExpenseShareTable.TABLE_NAME} es
        INNER JOIN ${ExpenseTable.TABLE_NAME} e
            ON es.${ExpenseShareTable.EXPENSE_ID} = e.${ExpenseTable.ID}
        INNER JOIN ${UserTable.TABLE_NAME} u
            ON es.${ExpenseShareTable.USER_ID} = u.${UserTable.ID}
        WHERE e.${ExpenseTable.TRIP_ID} = ?
        AND es.${ExpenseShareTable.IS_DELETED} = 0
        AND e.${ExpenseTable.IS_DELETED} = 0
        """.trimIndent(),
            arrayOf(groupId)
        )

        val shares = mutableListOf<ExpenseShare>()

        if (cursor.moveToFirst()) {
            do {
                val firstName = cursor.getString(cursor.getColumnIndexOrThrow("u_first_name")) ?: ""
                val lastName  = cursor.getString(cursor.getColumnIndexOrThrow("u_last_name"))  ?: ""

                shares.add(
                    ExpenseShare(
                        id            = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseShareTable.ID)),
                        expenseId     = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseShareTable.EXPENSE_ID)),
                        userId        = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseShareTable.USER_ID)),
                        userName      = "$firstName $lastName".trim(),
                        sharedAmount  = cursor.getDouble(cursor.getColumnIndexOrThrow(ExpenseShareTable.SHARED_AMOUNT)),
                        sharedPercent = cursor.getDouble(cursor.getColumnIndexOrThrow(ExpenseShareTable.SHARED_PERCENT)),
                        isIncluded    = cursor.getInt(cursor.getColumnIndexOrThrow(ExpenseShareTable.IS_INCLUDED)) == 1,
                        createdAt     = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseShareTable.CREATED_AT)),
                        updatedAt     = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseShareTable.UPDATED_AT)),
                        isDeleted     = cursor.getInt(cursor.getColumnIndexOrThrow(ExpenseShareTable.IS_DELETED)) == 1
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return shares
    }

    // DELETE — remove shares belonging to an expense that no longer exists
    fun deleteByExpenseId(expenseId: String) {
        val db = dbHelper.writableDatabase
        db.delete(
            ExpenseShareTable.TABLE_NAME,
            "${ExpenseShareTable.EXPENSE_ID} = ?",
            arrayOf(expenseId)
        )
    }
}