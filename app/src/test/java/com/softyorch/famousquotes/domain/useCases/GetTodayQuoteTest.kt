package com.softyorch.famousquotes.domain.useCases

import com.softyorch.famousquotes.data.network.response.QuoteResponse
import com.softyorch.famousquotes.domain.interfaces.IDatabaseService
import com.softyorch.famousquotes.domain.interfaces.IDatastore
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import com.softyorch.famousquotes.domain.utils.getTodayId
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
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
        MockKAnnotations.init(this)
        getTodayQuote = GetTodayQuote(dbService, storageService, datastore)
    }

    @After
    fun onAfter() {
        clearAllMocks()
    }

    @Test
    fun `When Getting Today Quote And Getting It From Service`() = runBlocking {
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

}
