package com.softyorch.famousquotes.domain.interfaces

import android.app.Activity
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import kotlinx.coroutines.flow.Flow

interface IBilling {
    suspend fun getPurchaseState(): Flow<Int>
    fun getSkuDetails(productId: String): SkuDetails?
    fun startConnection(hasConnection: (Boolean) -> Unit)
    suspend fun queryAvailableProducts(productsInApp: List<String>): List<SkuDetails>
    suspend fun launchPurchaseFlow(activity: Activity, skuDetails: SkuDetails): Int
    fun acknowledgePurchase(purchase: Purchase, acknowledgePurchases: (BillingResult) -> Unit)
    suspend fun queryPurchases(): List<Purchase>
}