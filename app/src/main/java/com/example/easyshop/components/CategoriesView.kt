package com.example.easyshop.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easyshop.GlobalNavigation
import com.example.easyshop.R
import com.example.easyshop.model.CategoryItem

@Composable
fun CategoriesView(modifier: Modifier = Modifier) {
    val categories = listOf(
        CategoryItem("Electronics", R.drawable.electronics,1),
        CategoryItem("Fashion", R.drawable.fashion,2),
        CategoryItem("Kitchen", R.drawable.kitchen,3),
        CategoryItem("Sports", R.drawable.sports,4),
        CategoryItem("Grocery", R.drawable.grocery,5)
    )

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            CategoryCard(category)
        }
    }
}

@Composable
fun CategoryCard(category: CategoryItem) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.size(90.dp).clickable{
            GlobalNavigation.navController.navigate("category-products/" + category.categoryId)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = category.imageRes),
                contentDescription = category.name,
                modifier = Modifier
                    .size(50.dp)
                    .padding(4.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = category.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
