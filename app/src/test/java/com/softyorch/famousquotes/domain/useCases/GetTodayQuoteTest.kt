package com.softyorch.famousquotes.domain.useCases

import com.softyorch.famousquotes.data.network.response.QuoteResponse
import com.softyorch.famousquotes.domain.interfaces.IDatabaseService
import com.softyorch.famousquotes.domain.interfaces.IDatastore
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import com.softyorch.famousquotes.utils.TestUtils
import com.softyorch.famousquotes.domain.utils.getTodayId
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class GetTodayQuoteTest {

    @RelaxedMockK
    private lateinit var dbService: IDatabaseService

    @RelaxedMockK
    private lateinit var storageService: IStorageService

    @RelaxedMockK
    private lateinit var datastore: IDatastore

    private lateinit var getTodayQuote: GetTodayQuote

    @Before
    fun onBefore() {
        TestUtils()
        MockKAnnotations.init(this)
        getTodayQuote = GetTodayQuote(dbService, storageService, datastore)
    }

    @After
    fun onAfter() {
        clearAllMocks()
    }

    @Test
    fun `When getting today Quote from Service`() = runBlocking {
        //Prepare test
        val id = getTodayId()
        val owner = "SoftYorch"
        val quote = "The test quote"
        val quoteResponse = QuoteResponse(
            id = id,
            owner = listOf("EN#$owner"),
            quote = listOf("EN#$quote")
        )

        //Given
        coEvery { dbService.getQuote(id) } returns quoteResponse
        coEvery { datastore.getImageSet() } returns flowOf(setOf())

        //When
        val result = getTodayQuote()

        //Then
        assert(result?.owner == owner && result.body == quote)
    }

    @Test
    fun `When getting today Quote from Service but is random Quote by bad id`() = runBlocking {
        //Prepare test
        val id = "1704099900000"
        val owner = "SoftYorch"
        val quote = "The test quote"
        val quoteResponse = QuoteResponse(
            id = id,
            owner = listOf("EN#$owner"),
            quote = listOf("EN#$quote")
        )

        //Given
        coEvery { dbService.getQuote(id) } returns null
        coEvery { dbService.getRandomQuote() } returns quoteResponse
        coEvery { datastore.getImageSet() } returns flowOf(setOf())

        //When
        val result = getTodayQuote(id)

        //Then
        assert(result?.owner == owner && result.body == quote)
    }

    @Test
    fun `When getting Today Quote and return null`() = runBlocking {
        //Prepare test
        val id = "1704099900000"

        //Given
        coEvery { dbService.getQuote(id) } returns null
        coEvery { dbService.getRandomQuote() } returns null
        coEvery { datastore.getImageSet() } returns flowOf(setOf())

        //When
        val result = getTodayQuote(id)

        //Then
        assert(result == null)
    }

    @Test
    fun `When getting Today Quote then getting Image and set image in quote model`() = runBlocking {
        //Prepare test
        val id = "1704099900000"
        val owner = "SoftYorch"
        val quote = "The test quote"
        val quoteResponse = QuoteResponse(
            id = id,
            owner = listOf("EN#$owner"),
            quote = listOf("EN#$quote")
        )

        //Given
        coEvery { dbService.getQuote(id) } returns quoteResponse
        coEvery { datastore.getImageSet() } returns flowOf(setOf())

        //When
        val result = dbService.getQuote(id)

        //Then
        assert(result?.imageUrl != null)
    }


    @Test
    fun `When getting Today Quote from service this is called once`() = runBlocking {

        //Given
        coEvery { dbService.getQuote(any()) } returns null
        coEvery { dbService.getRandomQuote() } returns null
        coEvery { datastore.getImageSet() } returns flowOf(setOf())

        //When
        getTodayQuote()

        //Then
        coVerify(exactly = 1) { dbService.getQuote(any()) }
    }

    @Test
    fun `When getting Today Quote from service and return null then getting Random Quote is called once`() = runBlocking {

        //Given
        coEvery { dbService.getQuote(any()) } returns null
        coEvery { dbService.getRandomQuote() } returns null
        coEvery { datastore.getImageSet() } returns flowOf(setOf())

        //When
        getTodayQuote()

        //Then
        coVerify(exactly = 1) { dbService.getRandomQuote() }
    }
}
