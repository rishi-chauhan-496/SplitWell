package com.example.splitbuddy

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.splitbuddy.data.local.database.Database
import com.example.splitbuddy.data.local.model.Expense
import com.example.splitbuddy.data.local.query.ExpenseQuery
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ExpenseQueryInstrumentedTest {

    private lateinit var context: Context
    private lateinit var database: Database
    private lateinit var query: ExpenseQuery

    private val expense = Expense(
        id = "E1",
        title = "Dinner",
        description = "qtweujf67ugjnb",
        amount = 1200.0,
        splitMethod = "amount",
        paidByUser = "U1",
        tripId = "T1",
        currencyCode = "INR",
        createdAt = "2026-03-25",
        updatedAt = "2026-03-25",
        isDeleted = false
    )

    @Before
    fun setUp() {

        context = ApplicationProvider.getApplicationContext()

        context.deleteDatabase("SplitBuddy.db")

        database = Database(context)
        query = ExpenseQuery(database)
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertExpense_whenValidDataProvided_shouldInsertSuccessfully() {

        val result = query.insertExpense(expense)

        assertTrue(result)
    }

    @Test
    @Throws(Exception::class)
    fun getExpenseByTripId_whenValidTripIdProvided_shouldReturnExpenseList() {

        query.insertExpense(expense)

        val result = query.getExpenseByTripId("T1")

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(expense, result[0])
    }

    @Test
    @Throws(Exception::class)
    fun getExpenseByPayer_whenValidUserIdProvided_shouldReturnExpenseList() {

        query.insertExpense(expense)

        val result = query.getExpenseByPayer("U1")

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(expense, result[0])
    }

    @Test
    @Throws(Exception::class)
    fun updateExpense_whenDataUpdated_shouldGetChangeValues() {

        query.insertExpense(expense)

        val updated = expense.copy(
            title = "Dinner Updated",
            amount = 1500.0,
            updatedAt = "2026-03-26"
        )

        val result = query.updateExpense(updated)
        val saved = query.getExpenseByTripId("T1")[0]

        assertTrue(result)
        assertEquals("Dinner Updated", saved.title)
        assertEquals(1500.0, saved.amount)
    }
}