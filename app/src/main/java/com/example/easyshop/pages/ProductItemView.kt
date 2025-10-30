package com.example.easyshop.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.easyshop.GlobalNavigation
import com.example.easyshop.model.ProductModel

@Composable
fun ProductItemView(modifier: Modifier = Modifier, product: ProductModel) {
    Card(
        modifier = modifier
            .clickable {
                GlobalNavigation.navController.navigate("product-details/"+product.id)
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = product.image.firstOrNull()),
                contentDescription = product.title,
                modifier = Modifier
                    .size(100.dp)
                    .padding(4.dp),
                contentScale = ContentScale.Crop
            )

            Text(
                text = product.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = product.price,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = product.actualPrice,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    style = TextStyle(textDecoration = TextDecoration.LineThrough)
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Add to cart"
                    )
                }
            }
        }
    }
}
