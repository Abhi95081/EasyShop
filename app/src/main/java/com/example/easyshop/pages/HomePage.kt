package com.example.easyshop.pages

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.easyshop.AppUtil
import com.example.easyshop.components.CategoriesView
import com.example.easyshop.components.HeaderView
import com.example.easyshop.data.DummyProducts
import com.example.easyshop.model.ProductModel

@Composable
fun HomePage(modifier: Modifier = Modifier) {
    val products = DummyProducts.productList.shuffled()
    val isDark = isSystemInDarkTheme()

    val bgGradient = if (isDark) {
        Brush.verticalGradient(
            listOf(Color(0xFF0F0F10), Color(0xFF1C1C1E))
        )
    } else {
        Brush.verticalGradient(
            listOf(Color(0xFFE3F2FD), Color(0xFFFFFFFF))
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bgGradient)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 12.dp)
            .navigationBarsPadding()
    ) {
        HeaderView(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(18.dp))

        SectionHeader("✨ Explore Categories")
        CategoriesView(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(26.dp))

        SectionHeader("🔥 Trending Now")
        ProductHorizontalList(products.take(6))

        Spacer(modifier = Modifier.height(30.dp))

        SectionHeader("💥 Best Deals for You")
        ProductHorizontalList(products.shuffled().take(6))

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 22.sp,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)
    )
}

@Composable
fun ProductHorizontalList(products: List<ProductModel>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(products) { product ->
            ProductCard(product)
        }
    }
}

@Composable
fun ProductCard(product: ProductModel) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    var isFavorite by remember { mutableStateOf(AppUtil.isFavorite(product.id)) }
    var isInCart by remember { mutableStateOf(AppUtil.isInCart(product.id)) }

    // 🧮 Calculate discount %
    val discountPercent = try {
        val actual = product.actualPrice.filter { it.isDigit() }.toDouble()
        val sale = product.price.filter { it.isDigit() }.toDouble()
        if (actual > 0) ((actual - sale) / actual * 100).toInt() else 0
    } catch (e: Exception) {
        0
    }

    val favScale by animateFloatAsState(
        targetValue = if (isFavorite) 1.3f else 1f,
        label = ""
    )

    val cartButtonColor by animateColorAsState(
        targetValue = if (isInCart)
            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        else Color.Transparent,
        label = ""
    )

    Card(
        modifier = Modifier
            .width(180.dp)
            .wrapContentHeight()
            .animateContentSize()
            .shadow(10.dp, RoundedCornerShape(18.dp))
            .clickable {
                // TODO: Navigate to Product Detail
            },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF1A1B1F) else Color.White
        ),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.LightGray.copy(alpha = 0.1f))
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = product.image.firstOrNull()),
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // ✨ Gradient overlay for style
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // 🎯 Discount Badge
                if (discountPercent > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (discountPercent >= 50)
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFFF7043), Color(0xFFFFB74D))
                                    )
                                else
                                    Brush.horizontalGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.primary,
                                            Color(0xFF80DEEA)
                                        )
                                    )
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$discountPercent% OFF",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // ❤️ Favorite Button (with glow)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = if (isFavorite)
                                    listOf(Color(0xFFFF1744), Color.Transparent)
                                else listOf(Color.Black.copy(alpha = 0.2f), Color.Transparent)
                            )
                        )
                        .clickable {
                            AppUtil.toggleFavorite(context, product.id)
                            isFavorite = AppUtil.isFavorite(product.id)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFFF1744)
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        modifier = Modifier.scale(favScale)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 🛍 Product Name
            Text(
                text = product.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(3.dp))

            // 💰 Prices
            Text(
                text = product.price,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "MRP: ${product.actualPrice}",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 🛒 Add to Cart Button
            Button(
                onClick = {
                    if (!isInCart) AppUtil.addToCart(context, product.id)
                    else AppUtil.removeFromCart(context, product.id)
                    isInCart = AppUtil.isInCart(product.id)
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .height(36.dp)
                    .fillMaxWidth()
                    .background(cartButtonColor),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isInCart)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else MaterialTheme.colorScheme.primary,
                    contentColor = if (isInCart)
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isInCart) "Added" else "Add to Cart",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
