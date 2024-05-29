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
                handlePurchase(it) {
                    purchaseState -> this.purchaseState = purchaseState
                }
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
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {

            writeLog(INFO, "BillingService: handlePurchase -> handlePurchase: PURCHASED")
            getAccessToWallpaper(Purchase.PurchaseState.PURCHASED)

            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                billingClient.acknowledgePurchase(acknowledgePurchaseParams.build()) { billingResult ->
                    getAccessToWallpaper(billingResult.responseCode)
                    writeLog(INFO, "BillingService: handlePurchase -> acknowledgePurchase: $billingResult")
                }
            } else {
                writeLog(INFO, "BillingService: handlePurchase -> isAcknowledged: ${purchase.isAcknowledged}")
            }

        } else {
            getAccessToWallpaper(purchase.purchaseState)
            writeLog(WARN, "BillingService: handlePurchase -> handlePurchase: ${purchase.purchaseState}")
        }
    }

    override suspend fun getPurchaseState(): Flow<Int> = flowOf(purchaseState)

    override fun getSkuDetails(productId: String): SkuDetails? {
        writeLog(WARN, "BillingService: getSkuDetails -> skuDetailsList: $skuDetailsList")
        return skuDetailsList?.find { it.sku == productId }
    }

    override fun startConnection(hasConnection: (Boolean) -> Unit) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                writeLog(WARN, "BillingService: startConnection -> onBillingServiceDisconnected")
                hasConnection(false)
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                writeLog(INFO, "BillingService: onBillingSetupFinished -> BillingResponseCode: ${billingResult.responseCode}")
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    hasConnection(true)
                }
            }
        })
    }

    override fun queryAvailableProducts() {
        val skuList = listOf("1716764400000", "1716850800000", "1716937200000")
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailList ->
            writeLog(WARN, "BillingService: queryAvailableProducts -> BillingResponseCode: ${billingResult.responseCode}")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailList != null) {
                this.skuDetailsList = skuDetailList
                skuDetailList.forEach {
                    writeLog(INFO, "BillingService: queryAvailableProducts -> skuDetailsList: $it")
                }
            }
        }
    }

    override fun launchPurchaseFlow(activity: Activity, skuDetails: SkuDetails): Int {
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()
        val launch = billingClient.launchBillingFlow(activity, flowParams)
        writeLog(INFO, "BillingService: launchPurchaseFlow -> LaunchBillingFlow: $ ${launch.responseCode}")
        return launch.responseCode
    }

    override fun acknowledgePurchase(purchase: Purchase, acknowledgePurchases: (BillingResult) -> Unit) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                acknowledgePurchases(billingResult)
                writeLog(INFO, "BillingService: acknowledgePurchase -> billingResult: $ $billingResult")
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    acknowledgePurchases(billingResult)
                }
            }
        }
    }

    override fun queryPurchases(purchasesList: (List<Purchase>) -> Unit) {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchasesList(purchases)
                purchases.forEach { purchase ->
                    handlePurchase(purchase) {
                        writeLog(INFO, "BillingService: queryPurchases -> purchase: is $it >>> $purchase")
                    }
                }
            } else {
                purchasesList(emptyList())
            }
        }
    }

}

interface IBilling {
    suspend fun getPurchaseState(): Flow<Int>
    fun getSkuDetails(productId: String): SkuDetails?
    fun startConnection(hasConnection: (Boolean) -> Unit)
    fun queryAvailableProducts()
    fun launchPurchaseFlow(activity: Activity, skuDetails: SkuDetails): Int
    fun acknowledgePurchase(purchase: Purchase, acknowledgePurchases: (BillingResult) -> Unit)
    fun queryPurchases(purchasesList: (List<Purchase>) -> Unit)
}
