package org.app.billions.billing

object PromotionConnector {
    var onPromotionReceived: ((String) -> Unit)? = null

    fun triggerPromotion(productId: String) {
        println(" Kotlin Connector received: $productId")
        onPromotionReceived?.invoke(productId)
    }
}