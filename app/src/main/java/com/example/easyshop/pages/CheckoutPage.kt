package com.example.easyshop.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easyshop.AppUtil
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutPage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val cartItems = AppUtil.getCartItems()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 🧾 Calculations
    val subtotal = remember(cartItems) {
        cartItems.sumOf {
            val price = it.product.price.filter { c -> c.isDigit() || c == '.' }.toDoubleOrNull() ?: 0.0
            price * it.quantity
        }
    }
    val discount = subtotal * 0.10
    val tax = (subtotal - discount) * 0.18
    val total = subtotal - discount + tax

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    )
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "🧾 Checkout",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 10.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Payable", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "₹%.2f".format(total),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Button(
                        onClick = {
                            if (cartItems.isEmpty()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("🛒 Your cart is empty!")
                                }
                            } else {
                                AppUtil.startPayment(total)
                            }
                        },
                        shape = MaterialTheme.shapes.medium,
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                    ) {
                        Text("Pay ₹%.2f".format(total), fontSize = 17.sp)
                    }
                }
            }
        }
    ) { paddingValues ->

        AnimatedVisibility(
            visible = cartItems.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LazyColumn(
                modifier = modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // 🏠 Delivery Address
                item {
                    InfoCard(title = "Delivery Address") {
                        Text("Abhishek Roushan\nCU Boys Hostel, Punjab\n140413")
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = {
                            AppUtil.showToast(context, "Edit address feature coming soon")
                        }) { Text("Change Address") }
                    }
                }

                // 💳 Payment Info
                item {
                    InfoCard(title = "Payment Method") {
                        Text("Razorpay (UPI, Card, Netbanking)")
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = {
                            AppUtil.showToast(context, "More payment methods coming soon")
                        }) { Text("Change Payment Method") }
                    }
                }

                // 📦 Order Summary
                item {
                    InfoCard(title = "Order Summary") {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            cartItems.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${item.product.title} x${item.quantity}")
                                    val price = item.product.price.filter { c -> c.isDigit() || c == '.' }.toDoubleOrNull() ?: 0.0
                                    Text("₹%.2f".format(price * item.quantity))
                                }
                            }

                            Divider(Modifier.padding(vertical = 8.dp))
                            PriceRow("Subtotal", subtotal)
                            PriceRow("Discount (10%)", -discount, MaterialTheme.colorScheme.error)
                            PriceRow("Tax (18%)", tax)
                            Divider(Modifier.padding(vertical = 8.dp))
                            PriceRow("Total", total, MaterialTheme.colorScheme.primary, bold = true)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun PriceRow(
    label: String,
    amount: Double,
    color: Color = MaterialTheme.colorScheme.onSurface,
    bold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (bold) MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            else MaterialTheme.typography.bodyMedium
        )
        Text(
            text = (if (amount < 0) "- " else "") + "₹%.2f".format(kotlin.math.abs(amount)),
            color = color,
            style = if (bold) MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            else MaterialTheme.typography.bodyMedium
        )
    }
}
