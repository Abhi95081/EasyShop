package com.example.easyshop.pages

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.easyshop.AppUtil
import com.example.easyshop.CartItem
import com.example.easyshop.GlobalNavigation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun CartPage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cartItems = remember { mutableStateListOf(*AppUtil.getCartItems().toTypedArray()) }

    val totalPrice by derivedStateOf {
        cartItems.sumOf {
            val price = it.product.price.filter { c -> c.isDigit() || c == '.' }.toDoubleOrNull() ?: 0.0
            price * it.quantity
        }
    }

    val animatedTotal by animateFloatAsState(targetValue = totalPrice.toFloat(), animationSpec = tween(500, easing = FastOutSlowInEasing), label = "")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Your Cart 🛒",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f)
                )
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                TotalCheckoutBar(animatedTotal = animatedTotal) {
                    AppUtil.showToast(context, "Proceeding to checkout ✨")
                    GlobalNavigation.navController.navigate("checkout")
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
                EmptyCartView()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(cartItems, key = { it.product.id }) { item ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(500)) + slideInVertically(initialOffsetY = { it / 2 }),
                            exit = fadeOut(animationSpec = tween(300))
                        ) {
                            PremiumCartItemView(
                                cartItem = item,
                                onQuantityChange = { newQty ->
                                    val index = cartItems.indexOf(item)
                                    if (index != -1) {
                                        cartItems[index] = item.copy(quantity = newQty)
                                    }
                                },
                                onRemove = {
                                    scope.launch {
                                        cartItems.remove(item)
                                        AppUtil.showToast(
                                            context,
                                            "${item.product.title} removed from cart 🗑️"
                                        )
                                    }
                                }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(120.dp)) }
                }
            }
        }
    }
}

@Composable
private fun TotalCheckoutBar(animatedTotal: Float, onCheckout: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Surface(shadowElevation = 12.dp, tonalElevation = 8.dp) {
        Row(
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
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Total Amount", color = Color.White, fontSize = 15.sp)
                Text(
                    "₹%.2f".format(animatedTotal),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            val scale by animateFloatAsState(targetValue = 1.05f, animationSpec = tween(800, easing = LinearEasing), label = "")
            Button(
                onClick = onCheckout,
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 28.dp, vertical = 12.dp),
                modifier = Modifier
                    .scale(scale)
                    .shadow(8.dp, RoundedCornerShape(50))
                    .background(
                        Brush.linearGradient(
                            listOf(Color.White.copy(alpha = glowAlpha), Color.White)
                        ),
                        shape = RoundedCornerShape(50)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Checkout", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        }
    }
}

@Composable
private fun EmptyCartView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🛒", fontSize = 60.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Your cart is empty",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Start shopping and fill it up!",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PremiumCartItemView(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    val context = LocalContext.current
    val priceAnim by animateFloatAsState(
        targetValue = (cartItem.quantity * (cartItem.product.price.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0)).toFloat(),
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = ""
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(cartItem.product.image.firstOrNull()),
                contentDescription = cartItem.product.title,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.LightGray.copy(alpha = 0.2f)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cartItem.product.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "₹%.2f".format(priceAnim),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                if (cartItem.quantity > 1) onQuantityChange(cartItem.quantity - 1)
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            cartItem.quantity.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.width(30.dp),
                            textAlign = TextAlign.Center
                        )

                        IconButton(
                            onClick = { onQuantityChange(cartItem.quantity + 1) },
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    IconButton(onClick = {
                        onRemove()
                        AppUtil.showToast(context, "Removed from cart ❌")
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
