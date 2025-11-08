package com.example.easyshop.pages

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.easyshop.AppUtil
import com.example.easyshop.model.ProductModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FavoritePage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var favoriteItems by remember { mutableStateOf(AppUtil.getFavoriteItems()) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Your Wishlist 💖",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (favoriteItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "No favourites yet!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "Add items to your wishlist 💎",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Snapshot copy to avoid concurrent modification crash
                val favSnapshot = remember(favoriteItems) { favoriteItems.toList() }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(favSnapshot, key = { it.id }) { product ->
                        var offsetX by remember { mutableStateOf(0f) }
                        var isRemoved by remember { mutableStateOf(false) }

                        AnimatedVisibility(
                            visible = !isRemoved,
                            exit = shrinkVertically(animationSpec = tween(400)) + fadeOut(tween(200))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .pointerInput(Unit) {
                                        detectHorizontalDragGestures(
                                            onHorizontalDrag = { _, dragAmount -> offsetX += dragAmount },
                                            onDragEnd = {
                                                if (offsetX < -180f) {
                                                    isRemoved = true
                                                    scope.launch {
                                                        AppUtil.toggleFavorite(context, product.id)
                                                        favoriteItems = AppUtil.getFavoriteItems()
                                                        snackbarHostState.showSnackbar("Removed from wishlist ❌")
                                                    }
                                                } else offsetX = 0f
                                            }
                                        )
                                    }
                            ) {
                                // 🟥 Swipe delete background
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(Color(0xFFFF6F61), Color(0xFFFF1744))
                                            )
                                        )
                                        .align(Alignment.CenterEnd),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.padding(end = 24.dp).size(28.dp)
                                    )
                                }

                                // 🧡 Card
                                PremiumFavoriteItemCard(
                                    product = product,
                                    modifier = Modifier
                                        .offset(x = offsetX.dp)
                                        .graphicsLayer { rotationZ = offsetX / 60f },
                                    onAddToCart = {
                                        scope.launch {
                                            isRemoved = true
                                            AppUtil.addToCart(context, product.id)
                                            delay(300)
                                            AppUtil.toggleFavorite(context, product.id)
                                            favoriteItems = AppUtil.getFavoriteItems()
                                            snackbarHostState.showSnackbar("Added to cart 🛒")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumFavoriteItemCard(
    product: ProductModel,
    modifier: Modifier = Modifier,
    onAddToCart: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val flyX = remember { Animatable(0f) }
    val flyY = remember { Animatable(0f) }
    val opacity = remember { Animatable(1f) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationX = flyX.value
                translationY = flyY.value
                alpha = opacity.value
            }
            .shadow(10.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 🖼 Product Image
            Image(
                painter = rememberAsyncImagePainter(product.image.firstOrNull()),
                contentDescription = product.title,
                modifier = Modifier
                    .size(95.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color.LightGray.copy(alpha = 0.3f), Color.Transparent)
                        )
                    ),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    product.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        product.price,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        product.actualPrice,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textDecoration = TextDecoration.LineThrough
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 🛒 Add to Cart Button
                Button(
                    onClick = {
                        // Fly diagonally up toward the cart ✈️
                        scope.launch {
                            flyX.animateTo(
                                200f,
                                animationSpec = tween(500, easing = FastOutSlowInEasing)
                            )
                            flyY.animateTo(
                                -300f,
                                animationSpec = tween(500, easing = FastOutSlowInEasing)
                            )
                            opacity.animateTo(0f, animationSpec = tween(300))
                            onAddToCart()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add to Cart", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
