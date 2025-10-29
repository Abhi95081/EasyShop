package com.example.easyshop.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.easyshop.data.DummyProducts
import com.example.easyshop.model.ProductModel

@Composable
fun CategoryProductPage(modifier: Modifier = Modifier, categoryId: String) {

    // Convert categoryId to actual category name (for demo purpose)
    val categoryName = when (categoryId.toInt()) {
        1 -> "Electronics"
        2 -> "Fashion"
        3 -> "Kitchen"
        4 -> "Sports"
        5 -> "Grocery"
        else -> ""
    }

    val filteredProducts = DummyProducts.productList.filter {
        it.category.equals(categoryName, ignoreCase = true)
    }

    Column(modifier = modifier.fillMaxSize().padding(12.dp)) {
        Text(
            text = "$categoryName Products",
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(filteredProducts) { product ->
                ProductCard(product)
            }
        }
    }
}

@Composable
fun ProductCard(product: ProductModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* navigate to product detail */ },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter(model = product.image.firstOrNull()),
                contentDescription = product.title,
                modifier = Modifier
                    .size(70.dp)
                    .padding(end = 12.dp),
                contentScale = ContentScale.Crop
            )
            Column {
                Text(text = product.title, fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Text(text = product.description, fontSize = 12.sp, maxLines = 2)
                Text(text = product.price, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
