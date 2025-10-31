package com.example.easyshop.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easyshop.AppUtil
import com.example.easyshop.GlobalNavigation
import com.example.easyshop.components.CartItemView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartPage(modifier: Modifier = Modifier) {
    val cartItems = remember { mutableStateListOf(*AppUtil.getCartItems().toTypedArray()) }
    val context = LocalContext.current

    val totalPrice by derivedStateOf {
        cartItems.sumOf {
            val price = it.product.price.filter { c -> c.isDigit() || c == '.' }.toDoubleOrNull() ?: 0.0
            price * it.quantity
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "🛒 Your Cart",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    shadowElevation = 10.dp,
                    tonalElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Total:",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "₹%.2f".format(totalPrice),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }

                        Button(
                            onClick = {
                                AppUtil.showToast(context, "Checkout")
                                GlobalNavigation.navController.navigate("checkout")
                                      },
                            shape = MaterialTheme.shapes.medium,
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                        ) {
                            Text("Checkout", fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (cartItems.isEmpty()) {
                // Empty Cart View
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🛍️ Your cart is empty",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Start adding your favorite products!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                // Cart List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems, key = { it.product.id }) { item ->
                        CartItemView(
                            ProductId = item.product.id,
                            qty = item.quantity.toLong(),
                            onQuantityChange = { newQty ->
                                val index = cartItems.indexOf(item)
                                if (index != -1) {
                                    cartItems[index] = item.copy(quantity = newQty.toInt())
                                }
                            },
                            onRemove = {
                                cartItems.remove(item)
                                AppUtil.showToast(
                                    context = context,
                                    message = "${item.product.title} removed from cart 🗑️"
                                )
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(100.dp)) // Space for bottom bar
                    }
                }
            }
        }
    }
}
