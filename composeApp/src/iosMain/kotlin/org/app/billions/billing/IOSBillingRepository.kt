//package org.app.billions.billing
//
//import Foundation
//import StoreKit
//import org.app.billions.ui.screens.inAppPurchase.BillingRepository
//import org.app.billions.ui.screens.inAppPurchase.PurchaseResult
//import shared
//
//class IOSBillingRepository: BillingRepository {
//
//    let themeProducts = ["theme_neon_coral", "theme_royal_blue", "theme_graphite_gold"]
//
//    func getThemes() async throws -> [Theme] {
//        let products = try await Product.products(for: themeProducts)
//            return products.map { product in
//                    Theme(
//                        id: product.id,
//                name: product.displayName,
//                isPurchased: UserDefaults.standard.bool(forKey: product.id)
//                )
//            }
//        }
//
//    func purchaseTheme(themeId: String) async throws -> PurchaseResult {
//        let products = try await Product.products(for: [themeId])
//            guard let product = products.first else {
//                return PurchaseResult.Error(message: "Product not found")
//            }
//
//            let result = try await product.purchase()
//                switch result {
//                    case .success(let verification):
//                    if case .verified(let transaction) = verification {
//                        await transaction.finish()
//                        UserDefaults.standard.set(true, forKey: themeId)
//                        return PurchaseResult.Success()
//                    }
//                    return PurchaseResult.Failure()
//                    default:
//                    return PurchaseResult.Failure()
//                }
//            }
//
//    func restorePurchases() async throws -> PurchaseResult {
//        for await result in Transaction.currentEntitlements {
//            if case .verified(let transaction) = result {
//                UserDefaults.standard.set(true, forKey: transaction.productID)
//            }
//        }
//        return PurchaseResult.Success()
//    }
//}
