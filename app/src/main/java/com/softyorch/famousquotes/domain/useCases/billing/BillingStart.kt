package com.softyorch.famousquotes.domain.useCases.billing

import com.softyorch.famousquotes.domain.interfaces.IBilling
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import kotlinx.coroutines.delay
import org.json.JSONObject
import javax.inject.Inject

class BillingStart @Inject constructor(
    private val billing: IBilling,
    private val storage: IStorageService,
) {
    suspend operator fun invoke(): BillingModel = startBilling().let { isConnected ->
        return if (isConnected) BillingModel(
            isConnected = isConnected,
            productsInApp = getAvailableProducts(getImageList()),
            productsPurchased = queryPurchases()
        ) else BillingModel.empty()
    }

    private suspend fun startBilling(): Boolean {
        var isConnected = false
        delay(1000)
        billing.startConnection { isConnected = it }
        return isConnected
    }

    private suspend fun getImageList(): List<String> = storage.getImageList() ?: listOf()

    private suspend fun getAvailableProducts(productsInApp: List<String>): List<String> =
        billing.queryAvailableProducts(productsInApp)
            .map { it.originalJson.jsonDetailsToProductId() }.toList()

    private suspend fun queryPurchases(): List<String> = billing.queryPurchases()
        .map { it.originalJson.jsonDetailsToProductId() }.toList()

    private fun String.jsonDetailsToProductId(): String {
        val jsonObject = JSONObject(this)
        return jsonObject.getString("productId")
    }
}
