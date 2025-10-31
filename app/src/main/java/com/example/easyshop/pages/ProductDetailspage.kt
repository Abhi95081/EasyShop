package com.example.easyshop.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.easyshop.AppUtil
import com.example.easyshop.data.DummyProducts

@Composable
fun ProductDetailsPage(modifier: Modifier = Modifier, productId: String) {

    val context = LocalContext.current
    val product = DummyProducts.productList.find { it.id == productId }

    if (product == null) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Product not found!", color = MaterialTheme.colorScheme.error)
        }
        return
    }

    // 🔹 Favorite state
    var isFavorite by remember { mutableStateOf(AppUtil.isFavorite(productId)) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🔹 Product Images
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            product.image.forEach { imgRes ->
                Card(
                    modifier = Modifier
                        .width(300.dp)
                        .height(250.dp),
                    shape = CardDefaults.shape,
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imgRes),
                        contentDescription = product.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = product.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Price and Favourite
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = product.price,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = product.actualPrice,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                style = TextStyle(textDecoration = TextDecoration.LineThrough)
            )

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = {
                AppUtil.toggleFavorite(context, productId)
                isFavorite = AppUtil.isFavorite(productId)
            }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Add to Favourite",
                    tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🔹 Add to Cart Button
        Button(
            onClick = { AppUtil.addToCart(context, productId) },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Cart",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = "Add to Cart", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🔹 Description
        Text("Product Description", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(product.description, fontSize = 16.sp, lineHeight = 22.sp)

        if (product.OtherDetails.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Product Details", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    product.OtherDetails.forEach { (key, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(key, fontSize = 14.sp)
                            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}
