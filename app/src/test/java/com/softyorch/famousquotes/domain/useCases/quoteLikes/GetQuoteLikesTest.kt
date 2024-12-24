package com.softyorch.famousquotes.domain.useCases.quoteLikes

import com.softyorch.famousquotes.domain.interfaces.IDatabaseListService
import com.softyorch.famousquotes.domain.utils.getTodayId
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test


class GetQuoteLikesTest {

    @RelaxedMockK
    private lateinit var dbService: IDatabaseListService

    private lateinit var getLikeQuotes: GetUserLikeQuote

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        getLikeQuotes = GetUserLikeQuote(dbService)
    }

    @After
    fun onAfter() {
        clearAllMocks()
    }

    @Test
    fun `When Getting Likes From Service And Return Data`() = runBlocking {
        //Prepare test
        val id = getTodayId()
        val returnFlow: Flow<Boolean> =
            flowOf(true)

        //Given
        coEvery { dbService.getUserLikeQuote(id) } returns returnFlow

        //When
        val result = getLikeQuotes(getTodayId())

        //Then
        val fakeResult = returnFlow.first()
        assert(result.first() == fakeResult)
    }

    @Test
    fun `When Getting Likes From Service And Return Null`() = runBlocking {
        //Prepare test
        val returnFlow = flowOf(null)

        //Given
        coEvery { dbService.getUserLikeQuote(any()) } returns returnFlow

        //When
        val result = getLikeQuotes(getTodayId())

        //Then
        val fakeResult = returnFlow.first()
        assert(result.first() == fakeResult)
    }


    @Test
    fun `When Getting Likes From Services And Only Called Once`() = runBlocking {
        //Given
        coEvery { dbService.getUserLikeQuote(any()) } returns flowOf()

        //When
        getLikeQuotes(getTodayId())

        //Then
        coVerify(exactly = 1) { dbService.getUserLikeQuote(any()) }
    }
}
