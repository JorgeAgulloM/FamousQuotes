package com.softyorch.famousquotes.domain.useCases

import com.softyorch.famousquotes.data.defaultDatabase.DefaultModel
import com.softyorch.famousquotes.data.network.response.QuoteResponse
import com.softyorch.famousquotes.domain.interfaces.IDatabaseQuoteService
import com.softyorch.famousquotes.domain.interfaces.IDefaultDatabase
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import com.softyorch.famousquotes.domain.utils.getTodayId
import com.softyorch.famousquotes.utils.TestUtils
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class GetTodayQuoteTest {

    @RelaxedMockK
    private lateinit var dbService: IDatabaseQuoteService

    @RelaxedMockK
    private lateinit var storageService: IStorageService

    @RelaxedMockK
    private lateinit var defaultDatabase: IDefaultDatabase

    private lateinit var getTodayQuote: GetTodayQuote

    @Before
    fun onBefore() {
        TestUtils()
        MockKAnnotations.init(this)
        getTodayQuote = GetTodayQuote(dbService, storageService, defaultDatabase)
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

        //When
        val result = getTodayQuote()

        //Then
        assert(result.owner == owner && result.body == quote)
    }

    @Test
    fun `When getting today Quote from Service but is random Quote by bad id`() = runBlocking {
        //Prepare test
        val id = "1704099900000"
        val owner = "SoftYorch"
        val quote = "The test quote"
        val defaultQuote = DefaultModel(
            owner = owner,
            quote = listOf("EN#$quote")
        )

        //Given
        coEvery { dbService.getQuote(id) } returns null
        coEvery { defaultDatabase.getDefaultQuote() } returns defaultQuote

        //When
        val result = getTodayQuote(id)

        //Then
        assert(result.owner == owner && result.body == quote)
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

        //When
        val result = getTodayQuote(id)

        //Then
        assert(!result.imageUrl.startsWith("http"))
    }


    @Test
    fun `When getting Today Quote from service this is called once`() = runBlocking {
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

        //When
        getTodayQuote(id)

        //Then
        coVerify(exactly = 1) { dbService.getQuote(any()) }
    }

    @Test
    fun `When getting Today Quote from service and return null then getting Default Quote is called once`() = runBlocking {
        //Prepare test
        val owner = "SoftYorch"
        val quote = "The test quote"
        val defaultQuote = DefaultModel(
            owner = owner,
            quote = listOf("EN#$quote")
        )

        //Given
        coEvery { dbService.getQuote(any()) } returns null
        coEvery { defaultDatabase.getDefaultQuote() } returns defaultQuote

        //When
        getTodayQuote()

        //Then
        coVerify(exactly = 1) { defaultDatabase.getDefaultQuote()}
    }
}
