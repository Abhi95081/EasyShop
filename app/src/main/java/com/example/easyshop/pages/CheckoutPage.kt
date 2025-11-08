package com.example.easyshop.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easyshop.AppUtil
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun CheckoutPage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val cartItems = AppUtil.getCartItems()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 🧾 Order Calculations
    val subtotal = remember(cartItems) {
        cartItems.sumOf {
            val price = it.product.price.filter { c -> c.isDigit() || c == '.' }.toDoubleOrNull() ?: 0.0
            price * it.quantity
        }
    }
    val discount = subtotal * 0.10
    val tax = (subtotal - discount) * 0.18
    val total = subtotal - discount + tax

    val animatedTotal by animateFloatAsState(
        targetValue = total.toFloat(),
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = ""
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.95f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f)
                            )
                        )
                    )
                    .padding(vertical = 18.dp)
            ) {
                Text(
                    text = "Checkout 🧾",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = cartItems.isNotEmpty(),
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 }),
                exit = fadeOut()
            ) {
                TotalPaymentBar(animatedTotal = animatedTotal) {
                    if (cartItems.isEmpty()) {
                        scope.launch { snackbarHostState.showSnackbar("🛒 Your cart is empty!") }
                    } else {
                        AppUtil.startPayment(animatedTotal.toDouble())
                    }
                }
            }
        }
    ) { paddingValues ->
        AnimatedVisibility(
            visible = cartItems.isNotEmpty(),
            enter = fadeIn(animationSpec = tween(600)) + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut()
        ) {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // 🏠 Address Section
                item {
                    PremiumInfoCard(
                        title = "Delivery Address",
                        icon = Icons.Default.LocationOn,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text("Abhishek Roushan\nCU Boys Hostel, Punjab - 140413", fontSize = 15.sp)
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { AppUtil.showToast(context, "Edit address coming soon") },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Change Address")
                        }
                    }
                }

                // 💳 Payment Method Section
                item {
                    PremiumInfoCard(
                        title = "Payment Method",
                        icon = Icons.Default.ThumbUp,
                        color = MaterialTheme.colorScheme.secondary
                    ) {
                        Text("Razorpay (UPI, Card, Netbanking)", fontSize = 15.sp)
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { AppUtil.showToast(context, "More payment methods soon") },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Change Method")
                        }
                    }
                }

                // 📦 Order Summary
                item {
                    PremiumInfoCard(
                        title = "Order Summary",
                        icon = Icons.Default.DateRange,
                        color = MaterialTheme.colorScheme.tertiary
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            cartItems.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${item.product.title} x${item.quantity}")
                                    val price =
                                        item.product.price.filter { it.isDigit() || it == '.' }
                                            .toDoubleOrNull() ?: 0.0
                                    Text("₹%.2f".format(price * item.quantity))
                                }
                            }

                            Divider(Modifier.padding(vertical = 6.dp))
                            PriceRow("Subtotal", subtotal)
                            PriceRow("Discount (10%)", -discount, MaterialTheme.colorScheme.error)
                            PriceRow("Tax (18%)", tax)
                            Divider(Modifier.padding(vertical = 6.dp))
                            PriceRow(
                                "Total",
                                total,
                                MaterialTheme.colorScheme.primary,
                                bold = true
                            )
                        }
                    }
                }
            }
        }

        if (cartItems.isEmpty()) EmptyCheckoutView()
    }
}

@Composable
fun PremiumInfoCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun TotalPaymentBar(animatedTotal: Float, onPayClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val glow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Surface(shadowElevation = 10.dp, tonalElevation = 8.dp) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Payable", color = Color.White, fontSize = 14.sp)
                    Text(
                        "₹%.2f".format(animatedTotal),
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp
                    )
                }

                Button(
                    onClick = onPayClick,
                    shape = RoundedCornerShape(40.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = glow),
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.shadow(8.dp, RoundedCornerShape(50))
                ) {
                    Text("Pay Now", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            }
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
            text = (if (amount < 0) "- " else "") + "₹%.2f".format(abs(amount)),
            color = color,
            style = if (bold) MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            else MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun EmptyCheckoutView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🛍️", fontSize = 60.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No items to checkout!",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Add something to your cart and come back 😊",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
