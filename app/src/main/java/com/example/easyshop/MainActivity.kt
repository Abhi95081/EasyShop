package com.example.easyshop

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.easyshop.model.OrderModel
import com.example.easyshop.ui.theme.EasyShopTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.razorpay.PaymentResultListener
import java.util.*

class MainActivity : ComponentActivity(), PaymentResultListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EasyShopTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(Modifier.padding(innerPadding))
                }
            }
        }
    }

    // ✅ Payment success callback
    override fun onPaymentSuccess(paymentId: String?) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val cartItems = AppUtil.getCartItems()
        val orderId = System.currentTimeMillis().toString()

        val order = OrderModel(
            id = orderId,
            userId = currentUser?.uid ?: "guest",
            items = cartItems.associate { it.product.id to it.quantity.toLong() },
            status = "CONFIRMED",
            address = "Abhishek Roushan\nCU Boys Hostel, Punjab\n140413"
        )

        // ✅ Save order to Firestore
        FirebaseFirestore.getInstance()
            .collection("orders")
            .document(orderId)
            .set(order)
            .addOnSuccessListener {
                AppUtil.clearCartAndAddToOrder()

                AlertDialog.Builder(this)
                    .setTitle("✅ Payment Successful")
                    .setMessage("Your order has been placed successfully!\nPayment ID: $paymentId")
                    .setPositiveButton("OK") { _, _ ->
                        GlobalNavigation.navController.popBackStack()
                        GlobalNavigation.navController.navigate("home")
                    }
                    .setCancelable(false)
                    .show()
            }
            .addOnFailureListener { e ->
                AlertDialog.Builder(this)
                    .setTitle("⚠️ Payment Processed, but Order Failed")
                    .setMessage("Payment succeeded but order could not be saved.\n${e.message}")
                    .setPositiveButton("OK", null)
                    .show()
            }
    }

    // ❌ Payment failure
    override fun onPaymentError(errorCode: Int, errorMessage: String?) {
        AppUtil.showToast(this, "❌ Payment Failed: $errorMessage")
    }
}
