package com.example.easyshop.pages

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.easyshop.components.CategoriesView
import com.example.easyshop.components.HeaderView
import com.example.easyshop.data.DummyProducts
import com.example.easyshop.model.ProductModel

@Composable
fun HomePage(modifier: Modifier = Modifier) {
    val products = DummyProducts.productList.shuffled()
    val isDark = isSystemInDarkTheme()

    // ✨ Gradient background
    val gradientBackground = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0B0C10),
                Color(0xFF1F2833),
                Color(0xFF121212)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF0F7FF),
                Color(0xFFFFFFFF)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradientBackground)
    ) {
        // Curved top background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                .background(
                    Brush.linearGradient(
                        colors = if (isDark)
                            listOf(Color(0xFF0B132B), Color(0xFF1C2541))
                        else
                            listOf(Color(0xFF1976D2), Color(0xFF63A4FF))
                    )
                )
        )

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            // Header Section (sits on top of curved background)
            HeaderView(modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(18.dp))

            // Floating Card for Categories
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    Text(
                        text = "🛍 Shop by Category",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    CategoriesView(modifier = Modifier.fillMaxWidth())
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 🔥 Trending Products
            SectionHeader("🔥 Trending Products")
            ProductHorizontalList(products.take(6))

            Spacer(modifier = Modifier.height(25.dp))

            // 💥 Best Deals
            SectionHeader("💥 Best Deals for You")
            ProductHorizontalList(products.shuffled().take(6))

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 22.sp,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun ProductHorizontalList(products: List<ProductModel>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(horizontal = 6.dp)
    ) {
        items(products) { product ->
            ProductCard(product)
        }
    }
}

@Composable
fun ProductCard(product: ProductModel) {
    val isDark = isSystemInDarkTheme()

    val cardGlow = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF66FCF1).copy(alpha = 0.15f),
                Color(0xFF45A29E).copy(alpha = 0.05f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFFFFF),
                Color(0xFFF0F0F0)
            )
        )
    }

    Card(
        modifier = Modifier
            .width(170.dp)
            .wrapContentHeight()
            .shadow(
                elevation = if (isDark) 12.dp else 8.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = if (isDark) Color(0xFF45A29E) else Color.Gray.copy(alpha = 0.3f),
                spotColor = if (isDark) Color(0xFF66FCF1) else Color.LightGray
            )
            .background(cardGlow, RoundedCornerShape(18.dp))
            .clickable { /* TODO: Handle click */ }
            .animateContentSize(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF0B0C10) else Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .height(130.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = product.image.firstOrNull()),
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Discount Tag
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "20% OFF",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Wishlist Icon
                IconButton(
                    onClick = { /* TODO: Wishlist */ },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = product.title,
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = product.price,
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            Text(
                text = "MRP: ${product.actualPrice}",
                style = TextStyle(fontSize = 12.sp, color = Color.Gray)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = { /* TODO: Add to Cart */ },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(36.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Add to Cart", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
