package com.softyorch.famousquotes

import com.softyorch.famousquotes.domain.useCases.GetDbVersionTest
import com.softyorch.famousquotes.domain.useCases.GetTodayQuoteTest
import com.softyorch.famousquotes.domain.useCases.quoteLikes.GetQuoteLikesTest
import com.softyorch.famousquotes.domain.useCases.quoteLikes.SetQuoteLikeTest
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite::class)
@SuiteClasses(
    GetQuoteLikesTest::class,
    SetQuoteLikeTest::class,
    GetDbVersionTest::class,
    GetTodayQuoteTest::class,
)
class MainTest