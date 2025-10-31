package com.example.easyshop

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.easyshop.ui.theme.EasyShopTheme
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener

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
    override fun onPaymentSuccess(p0: String?) {
        AppUtil.showToast(this,"✅ Payment Successful")
    }

    // ✅ Payment failure callback
    override fun onPaymentError(p0: Int, p1: String?) {
        AppUtil.showToast(this,"❌ Payment Failed")
    }
}
