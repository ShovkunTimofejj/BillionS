package org.app.billions.billing

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.app.billions.data.model.Theme
import org.app.billions.data.repository.ThemeRepository
import org.app.billions.ui.screens.inAppPurchase.BillingRepository
import org.app.billions.ui.screens.inAppPurchase.PurchaseResult
import platform.Foundation.NSSet
import platform.Foundation.NSString
import platform.Foundation.setWithArray
import platform.Foundation.*
import platform.StoreKit.*
import platform.darwin.NSObject
import kotlin.coroutines.resume

class IOSBillingRepository(
    private val themeRepository: ThemeRepository
) : BillingRepository {

    private val products = listOf(
        "theme_neon_coral",
        "theme_royal_blue",
        "theme_graphite_gold"
    )

    private var loadedProducts: List<SKProduct> = emptyList()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        val request = SKProductsRequest(products.toSet())
        request.delegate = object : NSObject(), SKProductsRequestDelegateProtocol {
            override fun productsRequest(
                request: SKProductsRequest,
                didReceiveResponse: SKProductsResponse
            ) {
                loadedProducts = didReceiveResponse.products.filterIsInstance<SKProduct>()
            }
        }
        request.start()
    }

    override suspend fun getThemes(): List<Theme> {
        return themeRepository.getThemes()
    }

    override suspend fun purchaseTheme(themeId: String): PurchaseResult =
        suspendCancellableCoroutine { cont ->
            val product = loadedProducts.find { it.productIdentifier == themeId }
            if (product == null) {
                cont.resume(PurchaseResult.Error("Product not found"))
                return@suspendCancellableCoroutine
            }

            val payment = SKPayment.paymentWithProduct(product)
            SKPaymentQueue.defaultQueue().addTransactionObserver(object : NSObject(),
                SKPaymentTransactionObserverProtocol {
                override fun paymentQueue(
                    queue: SKPaymentQueue,
                    updatedTransactions: List<*>
                ) {
                    updatedTransactions.forEach { txObj ->
                        val transaction = txObj as SKPaymentTransaction
                        when (transaction.transactionState) {
                            SKPaymentTransactionState.SKPaymentTransactionStatePurchased -> {
                                SKPaymentQueue.defaultQueue().finishTransaction(transaction)
                                GlobalScope.launch {
                                    themeRepository.purchaseTheme(themeId)
                                }
                                cont.resume(PurchaseResult.Success)
                            }
                            SKPaymentTransactionState.SKPaymentTransactionStateFailed -> {
                                SKPaymentQueue.defaultQueue().finishTransaction(transaction)
                                cont.resume(PurchaseResult.Failure)
                            }
                            else -> {}
                        }
                    }
                }
            })
            SKPaymentQueue.defaultQueue().addPayment(payment)
        }

    override suspend fun restorePurchases(): PurchaseResult =
        suspendCancellableCoroutine { cont ->
            SKPaymentQueue.defaultQueue().restoreCompletedTransactions()
            cont.resume(PurchaseResult.Success)
        }
}

