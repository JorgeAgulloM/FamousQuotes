package com.softyorch.famousquotes.domain.useCases.billing

data class BillingModel(
    val isConnected: Boolean,
    val productsInApp: List<String>,
    val productsPurchased: List<String>,
) {
    companion object {
        fun empty(): BillingModel = BillingModel(false, listOf(), listOf())
    }
}
