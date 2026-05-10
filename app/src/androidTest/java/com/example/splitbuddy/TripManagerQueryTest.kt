package com.example.splitbuddy

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.splitbuddy.data.local.database.Database
import com.example.splitbuddy.data.local.model.TripManager
import com.example.splitbuddy.data.local.query.TripManagerQuery
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TripManagerQueryInstrumentedTest {

    private lateinit var context: Context
    private lateinit var database: Database
    private lateinit var query: TripManagerQuery

    private val manager = TripManager(
        id = "TM1",
        tripId = "T1",
        userId = "U1",
        role = "member",
        isActive = true,
        joinedAt = "2026-03-25",
        leftAt = "",
        createdAt = "2026-03-25",
        updatedAt = "2026-03-25",
        isDeleted = false
    )

    @Before
    fun setUp() {

        context = ApplicationProvider.getApplicationContext()

        context.deleteDatabase("SplitBuddy.db")

        database = Database(context)
        query = TripManagerQuery(database)
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertTripManager_whenValidDataProvided_shouldInsertSuccessfully() {

        val result = query.insertTripManager(manager)

        assertTrue(result)
    }

    @Test
    @Throws(Exception::class)
    fun getTripManagerByUserIdAndTripId_whenValidUserIdAndTripIdProvided_shouldReturnCorrectValues() {

        query.insertTripManager(manager)

        val saved = query.getTripManagerByUserIdAndTripId("U1", "T1")

        assertNotNull(saved)
        assertEquals(manager, saved)
    }

    @Test
    @Throws(Exception::class)
    fun getTripManagerByUserId_whenValidUserIdProvided_shouldReturnCorrectValues() {

        val manager2 = manager.copy(id = "TM2", tripId = "T2")

        query.insertTripManager(manager)
        query.insertTripManager(manager2)

        val list = query.getTripManagerByUserId("U1")

        assertEquals(2, list.size)
    }

    @Test
    @Throws(Exception::class)
    fun getTripManagerByTripId_whenValidTripIdProvided_shouldReturnCorrectValues() {

        val manager2 = manager.copy(id = "TM3", userId = "U2")

        query.insertTripManager(manager)
        query.insertTripManager(manager2)

        val list = query.getTripManagerByTripId("T1")

        assertEquals(2, list.size)
    }

    @Test
    @Throws(Exception::class)
    fun updateTripManager_whenDataUpdated_shouldGetNewValues() {

        query.insertTripManager(manager)

        val updated = manager.copy(
            tripId = "T2",
            updatedAt = "2026-03-26"
        )

        val result = query.updateTripManager(updated)
        val saved = query.getTripManagerByUserIdAndTripId("U1", "T2")

        assertTrue(result)
        assertNotNull(saved)
        assertEquals("T2", saved?.tripId)
    }
}