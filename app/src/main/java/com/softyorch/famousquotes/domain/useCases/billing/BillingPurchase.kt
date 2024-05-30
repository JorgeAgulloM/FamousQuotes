package com.softyorch.famousquotes.domain.useCases.billing

import android.app.Activity
import com.softyorch.famousquotes.data.network.IBilling
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BillingPurchase @Inject constructor(
    private val billing: IBilling,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend operator fun invoke(productId: String, activity: Activity): Int =
        billing.getSkuDetails(productId)?.let { skuDetails ->
            return withContext(dispatcherIO) {
                billing.launchPurchaseFlow(activity, skuDetails)
            }
        } ?: -1
}