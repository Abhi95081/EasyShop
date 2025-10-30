package com.example.easyshop.model

import androidx.compose.material3.BottomAppBarState

data class ProductModel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val price: String = "",
    val actualPrice: String = "",
    val category: String = "",
    val image: List<Int> = emptyList(),
    val OtherDetails : Map<String,String> = mapOf()
)