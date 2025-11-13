package org.app.billions.billing

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
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

    private val delegate = IOSBillingDelegate(themeRepository)

    override suspend fun getThemes(): List<Theme> =
        themeRepository.getThemes()

    override suspend fun purchaseTheme(themeId: String): PurchaseResult =
        delegate.purchaseTheme(themeId)

    override suspend fun restorePurchases(): PurchaseResult =
        delegate.restorePurchases()
}

@OptIn(ExperimentalForeignApi::class)
class IOSBillingDelegate(
    private val themeRepository: ThemeRepository
) : NSObject(), SKProductsRequestDelegateProtocol, SKPaymentTransactionObserverProtocol {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val themeProducts = setOf("theme_neon_coral", "theme_royal_blue", "theme_graphite_gold")
    private val products = mutableMapOf<String, SKProduct>()
    private val purchaseContinuations = mutableMapOf<String, (PurchaseResult) -> Unit>()

    init {
        SKPaymentQueue.defaultQueue().addTransactionObserver(this)
        fetchProducts()
    }

    private fun fetchProducts() {
        val request = SKProductsRequest(productIdentifiers = themeProducts)
        request.delegate = this
        request.start()
    }

    suspend fun purchaseTheme(themeId: String): PurchaseResult =
        suspendCancellableCoroutine { continuation ->
            val product = products[themeId]
            if (product == null) {
                continuation.resume(PurchaseResult.Error("Product not found"))
                return@suspendCancellableCoroutine
            }
            purchaseContinuations[themeId] = { result -> continuation.resume(result) }
            SKPaymentQueue.defaultQueue().addPayment(SKPayment.paymentWithProduct(product))
        }

    suspend fun restorePurchases(): PurchaseResult =
        suspendCancellableCoroutine { continuation ->
            purchaseContinuations["restore"] = { result -> continuation.resume(result) }
            SKPaymentQueue.defaultQueue().restoreCompletedTransactions()
        }

    override fun productsRequest(request: SKProductsRequest, didReceiveResponse: SKProductsResponse) {
        didReceiveResponse.products?.forEach { any ->
            val product = any as? SKProduct ?: return@forEach
            val id = product.productIdentifier ?: return@forEach
            products[id] = product
        }
    }

    override fun paymentQueue(queue: SKPaymentQueue, updatedTransactions: List<*>) {
        updatedTransactions.forEach { any ->
            val transaction = any as? SKPaymentTransaction ?: return@forEach
            when (transaction.transactionState) {
                SKPaymentTransactionState.SKPaymentTransactionStatePurchased -> handlePurchased(transaction)
                SKPaymentTransactionState.SKPaymentTransactionStateFailed -> {
                    val themeId = transaction.payment.productIdentifier ?: return@forEach
                    purchaseContinuations.remove(themeId)?.invoke(PurchaseResult.Failure)
                    SKPaymentQueue.defaultQueue().finishTransaction(transaction)
                }
                SKPaymentTransactionState.SKPaymentTransactionStateRestored -> handlePurchased(transaction)
                else -> {}
            }
        }
    }

    private fun handlePurchased(transaction: SKPaymentTransaction) {
        val themeId = transaction.payment.productIdentifier ?: return
        val callback = purchaseContinuations.remove(themeId) ?: purchaseContinuations.remove("restore")

        scope.launch {
            themeRepository.purchaseTheme(themeId)
            themeRepository.setCurrentTheme(themeId)
            callback?.invoke(PurchaseResult.Success)
        }

        SKPaymentQueue.defaultQueue().finishTransaction(transaction)
    }
}