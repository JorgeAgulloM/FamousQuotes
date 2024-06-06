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
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.softyorch.famousquotes.domain.interfaces.IBilling
import com.softyorch.famousquotes.utils.LevelLog.ERROR
import com.softyorch.famousquotes.utils.LevelLog.INFO
import com.softyorch.famousquotes.utils.LevelLog.WARN
import com.softyorch.famousquotes.utils.writeLog
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class BillingServiceImpl @Inject constructor(@ApplicationContext private val context: Context):
    IBilling {

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
        skuDetailsList?.forEachIndexed { index, skuDetails ->
            writeLog(WARN, "BillingService: getSkuDetails -> skuDetails: ($index) $skuDetails")
        }
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
                    hasConnection(true)
                    writeLog(INFO, "BillingService: onBillingSetupFinished -> startConnection: 0 (OK)")
                } else {
                    writeLog(INFO, "BillingService: onBillingSetupFinished -> startConnection: ${billingResult.responseCode} (BAD)")
                }
            }
        })
    }

    override suspend fun queryAvailableProducts(productsInApp: List<String>): List<SkuDetails> =
        suspendCancellableCoroutine { cancelableCoroutine ->
            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(productsInApp).setType(BillingClient.SkuType.INAPP)
            billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailList != null) {
                    this.skuDetailsList = skuDetailList
                    cancelableCoroutine.resume(skuDetailList.toList())
                } else {
                    cancelableCoroutine.resume(emptyList())
                }
            }
        }

    override suspend fun launchPurchaseFlow(activity: Activity, skuDetails: SkuDetails): Int =
        suspendCancellableCoroutine { cancelableCoroutine ->
            try {
                val flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetails)
                    .build()
                val launch = billingClient.launchBillingFlow(activity, flowParams)
                writeLog(INFO, "BillingService: launchPurchaseFlow -> LaunchBillingFlow: $ ${launch.responseCode}")
                cancelableCoroutine.resume(launch.responseCode)
            } catch (ex: Exception) {
                cancelableCoroutine.resume(-1)
            }
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

    override suspend fun queryPurchases(): List<Purchase> =
        suspendCancellableCoroutine { cancelableCoroutine ->
            val queryPurchaseParams = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()

            billingClient.queryPurchasesAsync(queryPurchaseParams) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    purchases.forEach { purchase ->
                        handlePurchase(purchase) {
                            writeLog(INFO, "BillingService: queryPurchases -> purchase: is $it >>> $purchase")
                        }
                    }
                    cancelableCoroutine.resume(purchases.toList())
                } else {
                    cancelableCoroutine.resume(emptyList())
                }
            }
        }

}

/**
 * 0 (BillingClient.BillingResponseCode.OK): La solicitud fue exitosa.
 * 1 (BillingClient.BillingResponseCode.USER_CANCELED): El usuario canceló la operación.
 * 2 (BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE): El servicio de facturación no está disponible (por ejemplo, problemas de red).
 * 3 (BillingClient.BillingResponseCode.BILLING_UNAVAILABLE): La facturación en Google Play no está disponible.
 * 4 (BillingClient.BillingResponseCode.ITEM_UNAVAILABLE): El artículo solicitado no está disponible para la compra.
 * 5 (BillingClient.BillingResponseCode.DEVELOPER_ERROR): Error del desarrollador (por ejemplo, parámetros no válidos).
 * 6 (BillingClient.BillingResponseCode.ERROR): Error irrecuperable durante la operación.
 * 7 (BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED): El artículo ya está comprado.
 * 8 (BillingClient.BillingResponseCode.ITEM_NOT_OWNED): El artículo no está comprado por el usuario.
 * */