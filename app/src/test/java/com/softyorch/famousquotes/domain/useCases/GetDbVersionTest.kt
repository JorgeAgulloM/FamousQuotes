package com.softyorch.famousquotes.domain.useCases

import com.softyorch.famousquotes.domain.interfaces.IConfigService
import com.softyorch.famousquotes.domain.interfaces.IDatastore
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class GetDbVersionTest {

    @RelaxedMockK
    private lateinit var configService: IConfigService

    @RelaxedMockK
    private lateinit var datastore: IDatastore

    private lateinit var getDbVersion: GetDbVersion

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        getDbVersion = GetDbVersion(configService, datastore, Dispatchers.Unconfined)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun onAfter() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `When Compared Db Version From ConfigService And DataStore And Result Is Not Need Update`() =
        runTest {
            //Prepare test
            val confReturn = "1.0.0"
            val dsValue = "1.0.0"
            val dsReturn = flowOf(dsValue)

            //Given
            coEvery { configService.getUpdateDbVersion() } returns confReturn
            coEvery { datastore.getDbVersion() } returns dsReturn

            //When
            var result = false
            launch { result = getDbVersion.invoke() }
            advanceUntilIdle()

            //Then
            assert(!result)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `When Compared Db Version From ConfigService And DataStore And Result Is Need Update`() =
        runTest {
            //Prepare test
            val confReturn = "1.0.1"
            val dsValue = "1.0.0"
            val dsReturn = flowOf(dsValue)

            //Given
            coEvery { configService.getUpdateDbVersion() } returns confReturn
            coEvery { datastore.getDbVersion() } returns dsReturn

            //When
            var result = false
            launch { result = getDbVersion() }
            advanceUntilIdle()

            //Then
            assert(result)
        }

    @Test
    fun `When Compared Db Versions And DataStore is Called Once`() = runBlocking {
        //Given
        coEvery { datastore.getDbVersion() } returns flowOf()

        //When
        getDbVersion()

        //Then
        coVerify(exactly = 1) { datastore.getDbVersion() }
    }

    @Test
    fun `When Compared Db Versions And ConfigService is Called Once`() = runBlocking {
        //Given
        coEvery { configService.getUpdateDbVersion() } returns ""

        //When
        getDbVersion()

        //Then
        coVerify(exactly = 1) { configService.getUpdateDbVersion() }
    }
}
