package com.app.splitwell.data.local.query

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.app.splitwell.data.local.database.Database
import com.app.splitwell.data.local.database.Database.ExpenseTable
import com.app.splitwell.data.local.database.Database.UserTable
import com.app.splitwell.data.local.model.Expense
import com.app.splitwell.data.remote.expense.ExpenseResponse

class ExpenseQuery(private val dbHelper: Database) {

    // INSERT
    fun insertExpense(expense: ExpenseResponse): Boolean {
        val db = dbHelper.writableDatabase
        val cv = ContentValues()

        cv.put(ExpenseTable.ID, expense.id)
        cv.put(ExpenseTable.TITLE, expense.title)
        cv.put(ExpenseTable.DESCRIPTION, expense.description)
        cv.put(ExpenseTable.AMOUNT, expense.amount.toDoubleOrNull() ?: 0.0)
        cv.put(ExpenseTable.SPLIT_METHOD, expense.splitMethod)
        cv.put(ExpenseTable.PAID_BY_USER, expense.paidByUser)
        cv.put(ExpenseTable.CURRENCY_CODE, expense.currencyCode)
        cv.put(ExpenseTable.TRIP_ID, expense.groupId)
        cv.put(ExpenseTable.CREATED_AT, expense.createdAt)
        cv.put(ExpenseTable.UPDATED_AT, expense.updatedAt)
        cv.put(ExpenseTable.IS_DELETED, expense.isDeleted)

        return db.insertWithOnConflict(
            ExpenseTable.TABLE_NAME, null, cv,
            SQLiteDatabase.CONFLICT_REPLACE
        ) > 0
    }

    // UPDATE
    fun updateExpense(expense: Expense): Boolean {
        val db = dbHelper.writableDatabase
        val cv = ContentValues()

        cv.put(ExpenseTable.TITLE, expense.title)
        cv.put(ExpenseTable.DESCRIPTION, expense.description)
        cv.put(ExpenseTable.AMOUNT, expense.amount)
        cv.put(ExpenseTable.SPLIT_METHOD, expense.splitMethod)
        cv.put(ExpenseTable.PAID_BY_USER, expense.paidByUser)
        cv.put(ExpenseTable.UPDATED_AT, expense.updatedAt)
        cv.put(ExpenseTable.IS_DELETED, expense.isDeleted)

        return db.update(
            ExpenseTable.TABLE_NAME, cv,
            "${ExpenseTable.ID} = ?",
            arrayOf(expense.id)
        ) > 0
    }

    // SELECT LIST — JOIN with users to get paidByUserName
    fun getExpenseByTripId(tripId: String): List<Expense> {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT
                e.${ExpenseTable.ID},
                e.${ExpenseTable.TITLE},
                e.${ExpenseTable.DESCRIPTION},
                e.${ExpenseTable.AMOUNT},
                e.${ExpenseTable.SPLIT_METHOD},
                e.${ExpenseTable.PAID_BY_USER},
                e.${ExpenseTable.TRIP_ID},
                e.${ExpenseTable.CURRENCY_CODE},
                e.${ExpenseTable.CREATED_AT},
                e.${ExpenseTable.UPDATED_AT},
                e.${ExpenseTable.IS_DELETED},
                u.${UserTable.FIRST_NAME} AS u_first_name,
                u.${UserTable.LAST_NAME}  AS u_last_name
            FROM ${ExpenseTable.TABLE_NAME} e
            INNER JOIN ${UserTable.TABLE_NAME} u
                ON e.${ExpenseTable.PAID_BY_USER} = u.${UserTable.ID}
            WHERE e.${ExpenseTable.TRIP_ID} = ?
            AND e.${ExpenseTable.IS_DELETED} = 0
            """.trimIndent(),
            arrayOf(tripId)
        )

        val expenses = mutableListOf<Expense>()

        if (cursor.moveToFirst()) {
            do {
                val firstName = cursor.getString(cursor.getColumnIndexOrThrow("u_first_name")) ?: ""
                val lastName  = cursor.getString(cursor.getColumnIndexOrThrow("u_last_name"))  ?: ""

                expenses.add(
                    Expense(
                        id               = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.ID)),
                        title            = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.TITLE)),
                        description      = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.DESCRIPTION)),
                        amount           = cursor.getDouble(cursor.getColumnIndexOrThrow(ExpenseTable.AMOUNT)),
                        splitMethod      = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.SPLIT_METHOD)),
                        paidByUser       = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.PAID_BY_USER)),
                        paidByUserName   = "$firstName $lastName".trim(),
                        tripId           = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.TRIP_ID)),
                        currencyCode     = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.CURRENCY_CODE)),
                        createdAt        = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.CREATED_AT)),
                        updatedAt        = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.UPDATED_AT)),
                        isDeleted        = cursor.getInt(cursor.getColumnIndexOrThrow(ExpenseTable.IS_DELETED)) == 1
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return expenses
    }

    // SELECT SINGLE — JOIN with users to get paidByUserName
    fun getExpenseById(id: String): Expense? {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT
                e.${ExpenseTable.ID},
                e.${ExpenseTable.TITLE},
                e.${ExpenseTable.DESCRIPTION},
                e.${ExpenseTable.AMOUNT},
                e.${ExpenseTable.SPLIT_METHOD},
                e.${ExpenseTable.PAID_BY_USER},
                e.${ExpenseTable.TRIP_ID},
                e.${ExpenseTable.CURRENCY_CODE},
                e.${ExpenseTable.CREATED_AT},
                e.${ExpenseTable.UPDATED_AT},
                e.${ExpenseTable.IS_DELETED},
                u.${UserTable.FIRST_NAME} AS u_first_name,
                u.${UserTable.LAST_NAME}  AS u_last_name
            FROM ${ExpenseTable.TABLE_NAME} e
            INNER JOIN ${UserTable.TABLE_NAME} u
                ON e.${ExpenseTable.PAID_BY_USER} = u.${UserTable.ID}
            WHERE e.${ExpenseTable.ID} = ?
            """.trimIndent(),
            arrayOf(id)
        )

        var expense: Expense? = null

        if (cursor.moveToFirst()) {
            val firstName = cursor.getString(cursor.getColumnIndexOrThrow("u_first_name")) ?: ""
            val lastName  = cursor.getString(cursor.getColumnIndexOrThrow("u_last_name"))  ?: ""

            expense = Expense(
                id             = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.ID)),
                title          = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.TITLE)),
                description    = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.DESCRIPTION)),
                amount         = cursor.getDouble(cursor.getColumnIndexOrThrow(ExpenseTable.AMOUNT)),
                splitMethod    = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.SPLIT_METHOD)),
                paidByUser     = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.PAID_BY_USER)),
                paidByUserName = "$firstName $lastName".trim(),
                tripId         = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.TRIP_ID)),
                currencyCode   = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.CURRENCY_CODE)),
                createdAt      = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.CREATED_AT)),
                updatedAt      = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.UPDATED_AT)),
                isDeleted      = cursor.getInt(cursor.getColumnIndexOrThrow(ExpenseTable.IS_DELETED)) == 1
            )
        }

        cursor.close()
        return expense
    }

    fun getExpenseByPayer(paidByUser: String): List<Expense> {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM ${ExpenseTable.TABLE_NAME} WHERE ${ExpenseTable.PAID_BY_USER} = ?",
            arrayOf(paidByUser)
        )

        val expenses = mutableListOf<Expense>()

        if (cursor.moveToFirst()) {
            do {
                expenses.add(
                    Expense(
                        id           = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.ID)),
                        title        = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.TITLE)),
                        description  = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.DESCRIPTION)),
                        amount       = cursor.getDouble(cursor.getColumnIndexOrThrow(ExpenseTable.AMOUNT)),
                        splitMethod  = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.SPLIT_METHOD)),
                        paidByUser   = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.PAID_BY_USER)),
                        tripId       = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.TRIP_ID)),
                        currencyCode = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.CURRENCY_CODE)),
                        createdAt    = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.CREATED_AT)),
                        updatedAt    = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.UPDATED_AT)),
                        isDeleted    = cursor.getInt(cursor.getColumnIndexOrThrow(ExpenseTable.IS_DELETED)) == 1
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return expenses
    }

    // DELETE MISSING — remove expenses no longer returned for this group
    fun deleteMissing(tripId: String, freshExpenseIds: Set<String>): List<String> {
        val db = dbHelper.writableDatabase

        val cursor = db.rawQuery(
            "SELECT ${ExpenseTable.ID} FROM ${ExpenseTable.TABLE_NAME} WHERE ${ExpenseTable.TRIP_ID} = ?",
            arrayOf(tripId)
        )

        val staleIds = mutableListOf<String>()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.ID))
                if (id !in freshExpenseIds) staleIds.add(id)
            } while (cursor.moveToNext())
        }
        cursor.close()

        staleIds.forEach { id ->
            db.delete(ExpenseTable.TABLE_NAME, "${ExpenseTable.ID} = ?", arrayOf(id))
        }

        return staleIds
    }
}