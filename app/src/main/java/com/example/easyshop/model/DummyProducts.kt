package com.example.easyshop.data

import com.example.easyshop.R
import com.example.easyshop.model.ProductModel

object DummyProducts {
    val productList = listOf(
        ProductModel(
            id = "1",
            title = "Smartphone",
            description = "Latest Android 14 smartphone",
            price = "₹12,999",
            actualPrice = "₹15,999",
            category = "Electronics",
            image = listOf(R.drawable.phone, R.drawable.phone_back)
        ),
        ProductModel(
            id = "2",
            title = "Running Shoes",
            description = "Comfortable sports shoes",
            price = "₹1,999",
            actualPrice = "₹2,499",
            category = "Fashion",
            image = listOf(R.drawable.shoe, R.drawable.shoe_2)
        ),
        ProductModel(
            id = "3",
            title = "Cricket Bat",
            description = "Professional grade cricket bat",
            price = "₹1000",
            actualPrice = "₹3000",
            category = "Sports",
            image = listOf(R.drawable.bat)
        ),
        ProductModel(
            id = "4",
            title = "Mixer Grinder",
            description = "Powerful 500W mixer for your kitchen",
            price = "₹3,499",
            actualPrice = "₹4,999",
            category = "Kitchen",
            image = listOf(R.drawable.mixer)
        ),
        ProductModel(
            id = "5",
            title = "Maggie",
            description = "Plan Maggie noodles",
            price = "₹250",
            actualPrice = "₹500",
            category = "Grocery",
            image = listOf(R.drawable.maggie)
        )
    )
}
