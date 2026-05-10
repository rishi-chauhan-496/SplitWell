package com.example.splitbuddy

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.splitbuddy.data.local.database.Database
import com.example.splitbuddy.data.local.model.Trip
import com.example.splitbuddy.data.local.query.TripsQuery
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TripsQueryInstrumentedTest {

    private lateinit var context: Context
    private lateinit var database: Database
    private lateinit var tripsQuery: TripsQuery

    private val testTrip = Trip(
        id = "T1",
        tripTitle = "Goa Trip",
        createdAt = "2026-03-25",
        updatedAt = "2026-03-25",
        isDeleted = false
    )

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        context.deleteDatabase("SplitBuddy.db")

        database = Database(context)
        tripsQuery = TripsQuery(database)
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertTrip_whenValidTripProvided_shouldInsertSuccessfully() {

        // Act
        val result = tripsQuery.insertTrips(testTrip)

        // Assert
        assertTrue(result)
    }

    @Test
    @Throws(Exception::class)
    fun getTrip_whenValidTripIdProvided_shouldReturnCorrectTrip() {

        tripsQuery.insertTrips(testTrip)

        val savedTrip = tripsQuery.getTrips(testTrip.id)

        assertNotNull(savedTrip)
        assertEquals(testTrip, savedTrip)
    }

    @Test
    @Throws(Exception::class)
    fun updateTrip_whenTripUpdated_shouldGetNewValues() {

        tripsQuery.insertTrips(testTrip)

        val updatedTrip = testTrip.copy(
            tripTitle = "Manali Trip",
            updatedAt = "2026-03-26"
        )

        val result = tripsQuery.updateTrips(updatedTrip)
        val fetchedTrip = tripsQuery.getTrips(testTrip.id)

        assertTrue(result)
        assertEquals("Manali Trip", fetchedTrip?.tripTitle)
    }
}