package com.example.easyshop.data

import com.example.easyshop.R
import com.example.easyshop.model.ProductModel

object DummyProducts {

    private val baseProducts = listOf(
        ProductModel(
            id = "1",
            title = "Smartphone",
            description = "Experience cutting-edge performance with the latest Android 14 smartphone. Featuring a high-speed processor, vibrant AMOLED display, and a professional-grade camera system.",
            price = "₹120",
            actualPrice = "₹150",
            category = "Electronics",
            image = listOf(R.drawable.phone, R.drawable.phone_back,R.drawable.phone, R.drawable.phone_back),
            OtherDetails = mapOf(
                "Brand" to "TechNova",
                "Model" to "Nova X14",
                "RAM" to "8 GB",
                "Storage" to "128 GB",
                "Battery" to "5000 mAh",
                "Warranty" to "1 Year"
            )
        ),
        ProductModel(
            id = "2",
            title = "Running Shoes",
            description = "Designed for athletes and fitness enthusiasts, these shoes offer superior cushioning, breathable mesh fabric, and anti-slip soles for all terrains.",
            price = "₹199",
            actualPrice = "₹249",
            category = "Fashion",
            image = listOf(R.drawable.shoe, R.drawable.shoe_2,R.drawable.shoe, R.drawable.shoe_2),
            OtherDetails = mapOf(
                "Brand" to "StrideX",
                "Material" to "Breathable Mesh",
                "Sole" to "Rubber Grip",
                "Color" to "Black & Orange",
                "Warranty" to "6 Months"
            )
        ),
        ProductModel(
            id = "3",
            title = "Cricket Bat",
            description = "Professional grade English willow cricket bat, lightweight and perfectly balanced for power hitting and long innings.",
            price = "₹100",
            actualPrice = "₹300",
            category = "Sports",
            image = listOf(R.drawable.bat,R.drawable.bat,R.drawable.bat,R.drawable.bat),
            OtherDetails = mapOf(
                "Brand" to "Spartan Pro",
                "Material" to "English Willow",
                "Weight" to "1.2 kg",
                "Size" to "Full Size (Men)",
                "Warranty" to "3 Months"
            )
        ),
        ProductModel(
            id = "4",
            title = "Mixer Grinder",
            description = "Powerful 500W mixer grinder with stainless steel jars for blending, grinding, and mixing your kitchen needs efficiently.",
            price = "₹349",
            actualPrice = "₹499",
            category = "Kitchen",
            image = listOf(R.drawable.mixer,R.drawable.mixer,R.drawable.mixer,R.drawable.mixer),
            OtherDetails = mapOf(
                "Brand" to "HomeEase",
                "Power" to "500 Watts",
                "Material" to "Stainless Steel",
                "Speed Settings" to "3 Levels",
                "Warranty" to "2 Years"
            )
        ),
        ProductModel(
            id = "5",
            title = "Maggie",
            description = "Delicious instant noodles ready in just 2 minutes. Perfect for a quick snack or a light meal with your favorite veggies.",
            price = "₹250",
            actualPrice = "₹500",
            category = "Grocery",
            image = listOf(R.drawable.maggie,R.drawable.maggie,R.drawable.maggie,R.drawable.maggie),
            OtherDetails = mapOf(
                "Brand" to "Nestle",
                "Type" to "Instant Noodles",
                "Flavor" to "Classic Masala",
                "Cooking Time" to "2 Minutes",
                "Quantity" to "12 Packs"
            )
        )
    )

    // 🔁 Repeat baseProducts 8 times with unique IDs
    val productList: List<ProductModel> = (1..10).flatMap { round ->
        baseProducts.mapIndexed { index, product ->
            product.copy(id = "${round}-${index + 1}")
        }
    }
}
