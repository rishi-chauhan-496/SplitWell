package com.example.splitbuddy

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.splitbuddy.data.local.database.Database
import com.example.splitbuddy.data.local.model.Settlement
import com.example.splitbuddy.data.local.query.SettlementQuery
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SettlementQueryInstrumentedTest {

    private lateinit var context: Context
    private lateinit var database: Database
    private lateinit var query: SettlementQuery

    private val settlement = Settlement(
        id = "ST1",
        tripId = "T1",
        fromUserId = "U1",
        toUserId = "U2",
//        userFinalContribution = 1500.0,
//        userFinalSharedAmount = 1000.0,
        settlementAmt = 500.0,
        note = "qwerfghsertyu",
        createdAt = "2026-03-25",
        updatedAt = "2026-03-25",
        isDeleted = false
    )

    @Before
    fun setUp() {

        context = ApplicationProvider.getApplicationContext()

        context.deleteDatabase("SplitBuddy.db")

        database = Database(context)
        query = SettlementQuery(database)
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertSettlement_whenValidDataProvided_shouldInsertSuccessfully() {

        val result = query.insertSettlement(settlement)

        assertTrue(result)
    }

    @Test
    @Throws(Exception::class)
    fun getSettlementByTrip_whenValidTripIdProvided_shouldReturnSettlementList() {

        query.insertSettlement(settlement)

        val result = query.getSettlementByTrip("T1")

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(settlement, result[0])
    }

    @Test
    @Throws(Exception::class)
    fun getSettlementByUser_whenValidUserIdIdProvided_shouldReturnSettlementList() {

        query.insertSettlement(settlement)

        val result = query.getSettlementByUser("U1")

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(settlement, result[0])
    }

    @Test
    @Throws(Exception::class)
    fun getSettlementByTripAndUser_whenValidTripIdAndUserIdProvided_shouldReturnSettlementList() {

        query.insertSettlement(settlement)

        val result = query.getSettlementByTripAndUser("T1", "U1")

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(settlement, result[0])
    }

    @Test
    @Throws(Exception::class)
    fun updateSettlement_whenValuesUpdated_shouldGetChangesValues() {

        query.insertSettlement(settlement)

        val updated = settlement.copy(
            settlementAmt = 1000.0,
            updatedAt = "2026-03-26"
        )

        val result = query.updateSettlement(updated)
        val saved = query.getSettlementByTripAndUser("T1", "U1")[0]


        assertTrue(result)
        assertEquals(1000.0, saved.settlementAmt)
    }
}