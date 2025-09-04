package org.app.billions.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.app.billions.data.model.Theme
import org.app.billions.data.repository.ThemeRepository
import org.app.billions.ui.screens.inAppPurchase.BillingRepository
import org.app.billions.ui.screens.inAppPurchase.PurchaseResult

class AndroidBillingRepository(
    private val context: Context,
    private val themeRepository: ThemeRepository
) : BillingRepository {

    private val billingClient = BillingClient.newBuilder(context)
        .setListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                handlePurchases(purchases)
            }
        }
        .enablePendingPurchases()
        .build()

    private val themeProducts = listOf(
        "theme_neon_coral",
        "theme_royal_blue",
        "theme_graphite_gold"
    )

    init {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {}
            override fun onBillingSetupFinished(result: BillingResult) {}
        })
    }

    override suspend fun getThemes(): List<Theme> {
        return themeRepository.getThemes()
    }

    override suspend fun purchaseTheme(themeId: String): PurchaseResult {
        val productDetails = queryProduct(themeId) ?: return PurchaseResult.Error("Product not found")

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()
                )
            ).build()

        val activity = context as? Activity ?: return PurchaseResult.Error("No activity context")
        val billingResult = billingClient.launchBillingFlow(activity, flowParams)

        return if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            PurchaseResult.Success
        } else {
            PurchaseResult.Failure
        }
    }

    override suspend fun restorePurchases(): PurchaseResult {
        val result = billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )
        val purchases = result.purchasesList
        if (purchases.isNotEmpty()) {
            handlePurchases(purchases)
            return PurchaseResult.Success
        }
        return PurchaseResult.Failure
    }

    private suspend fun queryProduct(productId: String): ProductDetails? {
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            )
            .build()

        val productDetailsResult = billingClient.queryProductDetails(queryProductDetailsParams)
        return productDetailsResult.productDetailsList?.firstOrNull()
    }

    private fun handlePurchases(purchases: List<Purchase>) {
        purchases.forEach { purchase ->
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                val params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(params) {}

                val themeId = purchase.products.firstOrNull() ?: return@forEach
                GlobalScope.launch {
                    themeRepository.purchaseTheme(themeId)
                }
            }
        }
    }
}