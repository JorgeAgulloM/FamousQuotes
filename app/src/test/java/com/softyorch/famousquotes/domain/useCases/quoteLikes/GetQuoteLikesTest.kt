package com.softyorch.famousquotes.domain.useCases.quoteLikes

import com.softyorch.famousquotes.data.network.response.LikeQuoteResponse
import com.softyorch.famousquotes.domain.interfaces.IDatabaseService
import com.softyorch.famousquotes.domain.model.LikesQuote.Companion.toDomain
import com.softyorch.famousquotes.domain.utils.getTodayId
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test


class GetQuoteLikesTest {

    @RelaxedMockK
    private lateinit var dbService: IDatabaseService

    private lateinit var getLikeQuotes: GetQuoteLikes

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        getLikeQuotes = GetQuoteLikes(dbService)
    }

    @Test
    fun `When Getting Likes From Service And Return Data`() = runBlocking {
        // Prepare test
        val id = getTodayId()
        val returnFlow: Flow<LikeQuoteResponse?> = flowOf(LikeQuoteResponse(id = id, likes = 3, like = true))

        //Given
        coEvery { dbService.getLikeQuoteFlow(id) } returns returnFlow

        //When
        val result = getLikeQuotes()

        //Then
        val fakeResult = returnFlow.first()
        assert(result.first() == fakeResult?.toDomain())
        coVerify(exactly = 1) { dbService.getLikeQuoteFlow(id) }
    }
}