package com.softyorch.famousquotes.domain.useCases.quoteLikes

import com.softyorch.famousquotes.domain.interfaces.IDatabaseService
import com.softyorch.famousquotes.domain.model.LikesDTO
import com.softyorch.famousquotes.domain.model.LikesDTO.Companion.toData
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class SetQuoteLikeTest {

    @RelaxedMockK
    private lateinit var dbService: IDatabaseService

    private lateinit var setQuoteLike: SetQuoteLike

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        setQuoteLike = SetQuoteLike(dbService)
    }

    @Test
    fun `When Settings Like From User UI`() = runBlocking {
        //Prepare test
        val updateLikes = LikesDTO(true)

        //Given
        coEvery { dbService.likeDislikeQuote(updateLikes.toData()) } returns Unit

        //When
        setQuoteLike(updateLikes)

        //Then
        coVerify(exactly = 1) { dbService.likeDislikeQuote(updateLikes.toData()) }
    }
}
