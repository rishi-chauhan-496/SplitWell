package com.example.splitbuddy.data.local.query

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.splitbuddy.data.local.database.Database
import com.example.splitbuddy.data.local.database.Database.UserTable
import com.example.splitbuddy.data.local.model.User
import com.example.splitbuddy.data.remote.user.UserResponse

class UserQuery(private val dbHelper: Database) {

    // INSERT
    fun insertUser(user: UserResponse): Boolean {

        val db = dbHelper.writableDatabase
        val cv = ContentValues()

        cv.put(UserTable.ID, user.id)
        cv.put(UserTable.USER_NAME, user.username)
        cv.put(UserTable.FIRST_NAME, user.firstName)
        cv.put(UserTable.LAST_NAME, user.lastName)
        cv.put(UserTable.CONTACT, user.contact)
        cv.put(UserTable.EMAIL, user.email)
        cv.put(UserTable.SOCIAL_ID, user.socialMediaId)
        cv.put(UserTable.CREATED_AT, user.createdAt)
        cv.put(UserTable.UPDATED_AT, user.updatedAt)

        val result = db.insertWithOnConflict(
            UserTable.TABLE_NAME, null, cv,
            SQLiteDatabase.CONFLICT_REPLACE
        )

        return result != -1L
    }

    // UPDATE
    fun updateUser(user: User): Boolean {

        val db = dbHelper.writableDatabase
        val cv = ContentValues()

        cv.put(UserTable.USER_NAME, user.userName)
        cv.put(UserTable.FIRST_NAME, user.firstName)
        cv.put(UserTable.LAST_NAME, user.lastName)
        cv.put(UserTable.CONTACT, user.contact)
        cv.put(UserTable.UPDATED_AT, user.updatedAt)

        return db.update(UserTable.TABLE_NAME, cv, "${UserTable.ID} = ?", arrayOf(user.id)) > 0
    }

    // SELECT SINGLE USER
    fun getUser(userId: String): User? {

        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM ${UserTable.TABLE_NAME} WHERE ${UserTable.ID} = ?",
            arrayOf(userId)
        )

        var user: User? = null

        if (cursor.moveToFirst()) {
            user = User(
                id          = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.ID)) ?: "",
                userName    = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.USER_NAME)) ?: "",
                firstName   = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.FIRST_NAME)) ?: "",
                lastName    = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.LAST_NAME)) ?: "",
                contact     = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.CONTACT)) ?: "",
                email       = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.EMAIL)) ?: "",
                socialMediaId = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.SOCIAL_ID)) ?: "",
                isActive    = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.IS_ACTIVE)) == 1,
                createdAt   = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.CREATED_AT)) ?: "",
                updatedAt   = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.UPDATED_AT)) ?: "",
                isDeleted   = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.IS_DELETED)) == 1
            )
        }

        cursor.close()
        return user
    }

    fun getALLUser(): List<User> {

        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM ${UserTable.TABLE_NAME}",
            null
        )

        val user = mutableListOf<User>()

        if (cursor.moveToFirst()) {
            do {
                user.add(
                    User(
                        id          = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.ID)) ?: "",
                        userName    = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.USER_NAME)) ?: "",
                        firstName   = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.FIRST_NAME)) ?: "",
                        lastName    = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.LAST_NAME)) ?: "",
                        contact     = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.CONTACT)) ?: "",
                        email       = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.EMAIL)) ?: "",
                        socialMediaId = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.SOCIAL_ID)) ?: "",
                        isActive    = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.IS_ACTIVE)) == 1,
                        createdAt   = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.CREATED_AT)) ?: "",
                        updatedAt   = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.UPDATED_AT)) ?: "",
                        isDeleted   = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.IS_DELETED)) == 1
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return user
    }
}
