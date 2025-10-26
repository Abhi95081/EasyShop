package com.example.easyshop.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easyshop.components.BannerView
import com.example.easyshop.components.CategoriesView
import com.example.easyshop.components.HeaderView

@Composable
fun HomePage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 6.dp, vertical = 1.dp)
    ) {
        // Header (Welcome message + Search icon)
        HeaderView(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(12.dp))

        // Banner slider
        BannerView(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Categories Section
        Text(
            text = "Categories",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        CategoriesView(
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}
