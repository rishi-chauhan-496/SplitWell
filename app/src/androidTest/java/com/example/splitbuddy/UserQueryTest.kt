package com.example.splitbuddy

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.splitbuddy.data.local.database.Database
import com.example.splitbuddy.data.local.model.User
import com.example.splitbuddy.data.local.query.UserQuery
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class UserQueryInstrumentedTest {

    private lateinit var context: Context
    private lateinit var database: Database
    private lateinit var sut: UserQuery

    private val testUser = User(
        id = "U1",
        userName = "Rishi_1234",
        firstName = "Rishi",
        lastName = "Chauhan",
        contact = "9999999999",
        email = "abc@gmail.com",
        socialMediaId = "123456789",
        isActive = true,
        createdAt = "2026-03-17",
        updatedAt = "2026-03-17",
        isDeleted = false
    )

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        context.deleteDatabase("SplitBuddy.db")

        database = Database(context)
        sut = UserQuery(database)
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertUser_whenValidUserProvided_shouldInsertSuccessfully() {

        val result = sut.insertUser(testUser)

        assertTrue(result)
    }

    @Test
    @Throws(Exception::class)
    fun getUser_whenValidUserIdPass_shouldReturnCorrectUser() {

        sut.insertUser(testUser)

        val savedUser = sut.getUser(testUser.id)

        assertNotNull(savedUser)
        assertEquals(testUser, savedUser)
    }

    @Test
    @Throws(Exception::class)
    fun updateUser_whenUserUpdated_shouldGetNewValues() {

        // Arrange
        sut.insertUser(testUser)

        val updatedUser = testUser.copy(
            userName = "Amit Updated"
        )

        val result = sut.updateUser(updatedUser)
        val fetchedUser = sut.getUser(testUser.id)

        assertTrue(result)
        assertEquals("Amit Updated", fetchedUser?.userName)
    }
}