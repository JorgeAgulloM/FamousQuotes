package com.softyorch.famousquotes

import com.softyorch.famousquotes.domain.useCases.GetTodayQuoteTest
import com.softyorch.famousquotes.domain.useCases.quoteLikes.GetQuoteLikesTest
import com.softyorch.famousquotes.domain.useCases.quoteLikes.SetQuoteLikeTest
import com.softyorch.famousquotes.ui.home.HomeViewModelTest
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite::class)
@SuiteClasses(
    GetQuoteLikesTest::class,
    SetQuoteLikeTest::class,
    GetTodayQuoteTest::class,
    HomeViewModelTest::class,
)
class MainTest
