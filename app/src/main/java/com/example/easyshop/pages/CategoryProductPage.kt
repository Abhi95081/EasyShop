package com.example.easyshop.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easyshop.data.DummyProducts
import com.example.easyshop.model.ProductModel

@Composable
fun CategoryProductPage(modifier: Modifier = Modifier, categoryId: String) {

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
            itemsIndexed(filteredProducts.chunked(2)) { _, rowProducts ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (product in rowProducts) {
                        ProductItemView(
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp),
                            product = product
                        )
                    }

                    // if odd number of products, fill empty space
                    if (rowProducts.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
