package com.example.easyshop

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.example.easyshop.data.DummyProducts
import com.example.easyshop.model.OrderModel
import com.example.easyshop.model.ProductModel
import com.razorpay.Checkout
import org.json.JSONObject

// 🛒 Cart model
data class CartItem(
    val product: ProductModel,
    var quantity: Int = 1
)

object AppUtil {

    private val cartItems = mutableListOf<CartItem>()
    private val favoriteItems = mutableListOf<ProductModel>()

    // 🔹 Show Toast
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // 🛒 Add to cart
    fun addToCart(context: Context, productId: String) {
        val product = DummyProducts.productList.find { it.id == productId }

        if (product != null) {
            val existingItem = cartItems.find { it.product.id == productId }

            if (existingItem != null) {
                existingItem.quantity++
                showToast(context, "Increased quantity of ${product.title} to ${existingItem.quantity}")
            } else {
                cartItems.add(CartItem(product, 1))
                showToast(context, "${product.title} added to cart 🛒")
            }
        } else {
            showToast(context, "Product not found!")
        }
    }

    // 🛒 Get cart items
    fun getCartItems(): List<CartItem> = cartItems

    // ❤️ Toggle favourite
    fun toggleFavorite(context: Context, productId: String) {
        val product = DummyProducts.productList.find { it.id == productId }
        if (product == null) {
            showToast(context, "Product not found!")
            return
        }

        if (favoriteItems.any { it.id == productId }) {
            favoriteItems.removeIf { it.id == productId }
            showToast(context, "${product.title} removed from favourites 💔")
        } else {
            favoriteItems.add(product)
            showToast(context, "${product.title} added to favourites ❤️")
        }
    }

    // ❤️ Check if product is favourite
    fun isFavorite(productId: String): Boolean {
        return favoriteItems.any { it.id == productId }
    }

    // ❤️ Get all favourite items
    fun getFavoriteItems(): List<ProductModel> = favoriteItems

    fun razorpayApiKey(): String {
            return "rzp_test_Ra0xpSF5f1Zfn6"
    }

    fun startPayment(amount: Double) {
        try {
            val activity = GlobalNavigation.navController.context as? Activity
            if (activity == null) {
                Toast.makeText(
                    GlobalNavigation.navController.context,
                    "Unable to start payment — invalid context.",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            val checkout = Checkout()
            checkout.setKeyID(razorpayApiKey()) // Your Razorpay Key ID (keep it secure)

            val options = JSONObject().apply {
                put("name", "EasyShop")
                put("description", "Order Payment")
                put("currency", "INR") // ✅ Use INR for Indian payments
                put("amount", (amount * 100).toInt()) // Razorpay expects amount in paise (₹1 = 100)
                put("theme.color", "#5C6BC0")
                put("image", "https://yourapp.com/logo.png")

                val prefill = JSONObject().apply {
                    put("email", "user@example.com")
                    put("contact", "9876543210")
                }
                put("prefill", prefill)
            }

            checkout.open(activity, options)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                GlobalNavigation.navController.context,
                "Error in payment: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun clearCartAndAddToOrder() {
        if (cartItems.isEmpty()) return

        val order = OrderModel(
            id = System.currentTimeMillis().toString(),
            userId = "dummyUserId",
            items = cartItems.associate { it.product.id to it.quantity.toLong() },
            status = "ORDERED",
            address = "CU Boys Hostel, Punjab"
        )

        // 🧹 Clear the cart
        cartItems.clear()

        println("✅ Order Placed Successfully: $order")
    }


}
