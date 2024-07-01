package com.softyorch.famousquotes.ui.home

import com.softyorch.famousquotes.core.ISend
import com.softyorch.famousquotes.core.Intents
import com.softyorch.famousquotes.core.InternetConnection
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import com.softyorch.famousquotes.domain.model.FamousQuoteModel
import com.softyorch.famousquotes.domain.model.LikesDTO
import com.softyorch.famousquotes.domain.model.LikesQuote
import com.softyorch.famousquotes.domain.useCases.GetTodayQuote
import com.softyorch.famousquotes.domain.useCases.billing.BillingPurchase
import com.softyorch.famousquotes.domain.useCases.billing.BillingStart
import com.softyorch.famousquotes.domain.useCases.quoteLikes.GetQuoteLikes
import com.softyorch.famousquotes.domain.useCases.quoteLikes.SetQuoteLike
import com.softyorch.famousquotes.domain.utils.getTodayId
import com.softyorch.famousquotes.ui.screens.home.HomeActions
import com.softyorch.famousquotes.ui.screens.home.HomeViewModel
import com.softyorch.famousquotes.utils.TestUtils
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @RelaxedMockK
    private lateinit var selectQuote: GetTodayQuote

    @RelaxedMockK
    private lateinit var getLikes: GetQuoteLikes

    @RelaxedMockK
    private lateinit var setLike: SetQuoteLike

    @RelaxedMockK
    private lateinit var billingStart: BillingStart

    @RelaxedMockK
    private lateinit var billingLaunch: BillingPurchase

    @RelaxedMockK
    private lateinit var storage: IStorageService

    @RelaxedMockK
    private lateinit var sendImpl: ISend

    @RelaxedMockK
    private lateinit var hasConnection: InternetConnection

    @RelaxedMockK
    private lateinit var intents: Intents

    private lateinit var homeViewModel: HomeViewModel

    @Before
    fun onBefore() {
        TestUtils()
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher = Dispatchers.Unconfined)
        homeViewModel = HomeViewModel(
            selectQuote = selectQuote,
            getLikes = getLikes,
            setLike = setLike,
            billingStartUseCase = billingStart,
            billingLaunchPurchase = billingLaunch,
            storage = storage,
            dispatcherIO = Dispatchers.Unconfined,
            sendImpl = sendImpl,
            hasConnection = hasConnection,
            intents = intents,
        )
    }

    @After
    fun onAfter() {
        clearAllMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `When start view model then getting Today Quote`() = runTest {
        //Prepare test
        val id = getTodayId()
        val owner = "SoftYorch"
        val quote = "The test quote"
        val returnQuote = FamousQuoteModel(
            id = id,
            owner = owner,
            body = quote,
            imageUrl = ""
        )

        //Given
        coEvery { selectQuote() } returns returnQuote

        //When
        launch { homeViewModel.onCreate() }
        advanceUntilIdle()

        //Then
        val result = homeViewModel.uiState.value.quote
        assert(result.body == quote)
    }

    @Test
    fun `When start view model then getting Likes Quote`() = runTest {
        //Prepare test
        val id = getTodayId()
        val likesQuote = LikesQuote(likes = 2, isLike = true)
        val returnFlowLikes = flowOf(likesQuote)

        //Given
        coEvery { getLikes(id) } returns returnFlowLikes

        //When
        launch { homeViewModel.onCreate() }
        advanceUntilIdle()

        //Then
        val result = homeViewModel.likesState.value
        assert(result.likes == likesQuote.likes)
    }

    @Test
    fun `When user request new quote then calling Random Quote`() = runTest {
        //Prepare test
        val id = getTodayId()
        val owner = "SoftYorch"
        val quote = "The test random quote"
        val returnQuote = FamousQuoteModel(
            id = id,
            owner = owner,
            body = quote,
            imageUrl = ""
        )

        //Given
        coEvery { selectQuote.getRandomQuote() } returns returnQuote

        //When
        launch {
            homeViewModel.onCreate()
            homeViewModel.showInterstitialOnlyForTesting()
            homeViewModel.onActions(HomeActions.New())
        }
        advanceUntilIdle()

        //Then
        val result = homeViewModel.uiState.value.quote
        assert(result.body == returnQuote.body)
    }

    @Test
    fun `When user push the Like Button then send like to service`() = runTest {
        //Prepare test
        val id = getTodayId()
        val likesDTO = LikesDTO(id = id, isLike = true)

        //Given
        coEvery { setLike(likesDTO) } returns Unit

        //When
        launch { homeViewModel.onActions(HomeActions.Like()) }
        advanceUntilIdle()

        //Then
        coVerify(exactly = 1) { setLike(likesDTO) }
    }

}