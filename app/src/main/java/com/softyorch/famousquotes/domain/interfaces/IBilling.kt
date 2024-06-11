package com.softyorch.famousquotes.domain.interfaces

import android.app.Activity
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import kotlinx.coroutines.flow.Flow

interface IBilling {
    suspend fun getPurchaseState(): Flow<Int>
    fun getProductDetails(productId: String): ProductDetails?
    fun startConnection(hasConnection: (Boolean) -> Unit)
    suspend fun queryAvailableProducts(productsInApp: List<String>): List<ProductDetails>
    suspend fun launchPurchaseFlow(activity: Activity, productDetails: ProductDetails): Int
    fun acknowledgePurchase(purchase: Purchase, acknowledgePurchases: (BillingResult) -> Unit)
    suspend fun queryPurchases(): List<Purchase>
}