package com.softyorch.famousquotes.data.network

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.softyorch.famousquotes.utils.LevelLog.ERROR
import com.softyorch.famousquotes.utils.LevelLog.INFO
import com.softyorch.famousquotes.utils.LevelLog.WARN
import com.softyorch.famousquotes.utils.writeLog
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class BillingServiceImpl @Inject constructor(@ApplicationContext private val context: Context): IBilling {

    private var purchaseState: Int = Purchase.PurchaseState.UNSPECIFIED_STATE

    private val purchaseUpdateListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            purchases.forEach {
                handlePurchase(it) { purchaseState -> this.purchaseState = purchaseState }
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            writeLog(WARN, "BillingService: PurchasesUpdatedListener -> UserCanceled")
        } else {
            writeLog(ERROR, "BillingService: PurchasesUpdatedListener -> Error")
        }
    }

    private val pendingPurchaseParams = PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()

    private val billingClient = BillingClient.newBuilder(context).setListener(purchaseUpdateListener).enablePendingPurchases(pendingPurchaseParams).build()

    private var skuDetailsList: List<SkuDetails>? = null

    private fun handlePurchase(purchase: Purchase, getAccessToWallpaper: (Int) -> Unit) {
        when (purchase.purchaseState) {
            Purchase.PurchaseState.PURCHASED -> {
                getAccessToWallpaper(Purchase.PurchaseState.PURCHASED)
                if (!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams) {
                        writeLog(INFO, "BillingService: handlePurchase -> acknowledgePurchase: $it")
                    }
                }
            }
            else -> getAccessToWallpaper(purchase.purchaseState)
        }
    }

    override fun getPurchaseState(): Flow<Int> = flowOf(purchaseState)

    override fun getSkuDetails(productId: String): SkuDetails? {
        return skuDetailsList?.find { it.sku == productId }
    }

    override fun startConnection(hasConnection: (Boolean) -> Unit) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                writeLog(WARN, "BillingService: startConnection -> onBillingServiceDisconnected")
                hasConnection(false)
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    writeLog(INFO, "BillingService: onBillingSetupFinished -> BillingResponseCode.OK")
                    hasConnection(true)
                }
            }
        })
    }

    override fun queryAvailableProducts() {
        val skuList = listOf("1716764400000")
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailList != null) {
                this.skuDetailsList = skuDetailList
                writeLog(INFO, "BillingService: queryAvailableProducts -> BillingResponseCode.OK")
                writeLog(INFO, "BillingService: queryAvailableProducts -> skuDetailsList: $skuDetailList")
            } else {
                writeLog(WARN, "BillingService: queryAvailableProducts -> BillingResponseCode: ${billingResult.responseCode}")
                writeLog(WARN, "BillingService: queryAvailableProducts -> skuDetailsList: $skuDetailList")
            }
        }
    }

    override fun launchPurchaseFlow(activity: Activity, skuDetails: SkuDetails) {
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()
        billingClient.launchBillingFlow(activity, flowParams)
    }

}

interface IBilling {
    fun getPurchaseState(): Flow<Int>
    fun getSkuDetails(productId: String): SkuDetails?
    fun startConnection(hasConnection: (Boolean) -> Unit)
    fun queryAvailableProducts()
    fun launchPurchaseFlow(activity: Activity, skuDetails: SkuDetails)
}
