package com.example.easyshop.pages

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.easyshop.AppUtil
import com.example.easyshop.GlobalNavigation
import com.example.easyshop.components.BannerView
import com.example.easyshop.components.CategoriesView
import com.example.easyshop.components.HeaderView
import com.example.easyshop.data.DummyProducts
import com.example.easyshop.model.ProductModel

@Composable
fun HomePage(modifier: Modifier = Modifier) {
    val products = DummyProducts.productList.shuffled()
    val isDark = isSystemInDarkTheme()

    val bgGradient = Brush.verticalGradient(
        if (isDark)
            listOf(Color(0xFF0F0F10), Color(0xFF1C1C1E))
        else
            listOf(Color(0xFFEAF3FF), Color.White)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bgGradient)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 12.dp)
    ) {
        // ---------- HEADER ----------
        HeaderView(modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(16.dp))

        // ---------- 🌟 BANNER VIEW HERE ----------
        BannerView(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(22.dp))

        // ---------- CATEGORIES ----------
        SectionHeader("✨ Explore Categories")
        CategoriesView(modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(26.dp))

        // ---------- TRENDING ----------
        SectionHeader("🔥 Trending Now")
        ProductHorizontalList(products.take(6)) { id ->
            GlobalNavigation.navController.navigate("product-details/$id")
        }

        Spacer(Modifier.height(30.dp))

        // ---------- BEST DEALS ----------
        SectionHeader("💥 Best Deals for You")
        ProductHorizontalList(products.shuffled().take(6)) { id ->
            GlobalNavigation.navController.navigate("product-details/$id")
        }

        Spacer(Modifier.height(100.dp))
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 18.dp)
    )
}

// -----------------------------------------------------
// PRODUCT HORIZONTAL LIST
// -----------------------------------------------------
@Composable
fun ProductHorizontalList(
    products: List<ProductModel>,
    onProductClick: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(products) { product ->
            ProductCard(product, onProductClick)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ProductCard(
    product: ProductModel,
    onProductClick: (String) -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    var pressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )

    var isFavorite by remember { mutableStateOf(AppUtil.isFavorite(product.id)) }
    var isInCart by remember { mutableStateOf(AppUtil.isInCart(product.id)) }

    val discountPercent = try {
        val actual = product.actualPrice.filter(Char::isDigit).toDouble()
        val sale = product.price.filter(Char::isDigit).toDouble()
        ((actual - sale) / actual * 100).toInt().coerceAtLeast(0)
    } catch (_: Exception) { 0 }

    Card(
        modifier = Modifier
            .width(175.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .combinedClickable(
                onClick = { onProductClick(product.id) },
                onLongClick = {}
            )
            .pointerInteropFilter {
                pressed = it.action != android.view.MotionEvent.ACTION_UP
                false
            },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF1C1D22) else Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {

            // ---------------- IMAGE ----------------
            Box(
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.LightGray.copy(alpha = 0.1f))
            ) {
                Image(
                    painter = rememberAsyncImagePainter(product.image.firstOrNull()),
                    contentDescription = product.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // ⭐ Discount Badge
                if (discountPercent > 0) {
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.TopStart)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            "$discountPercent% OFF",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // ❤️ Favorite button
                IconButton(
                    onClick = {
                        AppUtil.toggleFavorite(context, product.id)
                        isFavorite = AppUtil.isFavorite(product.id)
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .size(34.dp)
                        .align(Alignment.TopEnd)
                        .background(
                            Color.Black.copy(alpha = 0.22f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        tint = if (isFavorite) Color(0xFFFF1744) else Color.White,
                        contentDescription = "Favorite"
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ---------------- TITLE ----------------
            Text(
                text = product.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(6.dp))

            // ---------------- PRICE ----------------
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    product.price,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.width(6.dp))

                Text(
                    product.actualPrice,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textDecoration = TextDecoration.LineThrough
                )
            }

            Spacer(Modifier.height(12.dp))

// ---------------- ADD BUTTON ----------------
            Button(
                onClick = {
                    if (!isInCart) {
                        AppUtil.addToCart(context, product.id)
                        Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show()
                    } else {
                        AppUtil.removeFromCart(context, product.id)
                        Toast.makeText(context, "Removed from Cart", Toast.LENGTH_SHORT).show()
                    }
                    isInCart = AppUtil.isInCart(product.id)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),   // Bigger button for easier tapping
                shape = RoundedCornerShape(12.dp), // Softer corner
                elevation = ButtonDefaults.buttonElevation(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)   // Fixed size (looks cleaner)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "Add to Cart",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

        }
    }
}
