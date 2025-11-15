package com.example.easyshop

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.easyshop.Screen.AuthScreen
import com.example.easyshop.Screen.HomeScreen
import com.example.easyshop.Screen.LoginScreen
import com.example.easyshop.Screen.SignupScreen
import com.example.easyshop.pages.CategoryProductPage
import com.example.easyshop.pages.CheckoutPage
import com.example.easyshop.pages.Help
import com.example.easyshop.pages.OrderPages
import com.example.easyshop.pages.ProductDetailsPage
import com.example.easyshop.pages.SearchPage
import com.example.easyshop.pages.Setting
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    GlobalNavigation.navController = navController

    val isLoggedIn = Firebase.auth.currentUser!=null
    val firstpage = if(isLoggedIn) "home" else "auth"

    NavHost(navController = navController,startDestination = firstpage){

        composable("auth"){
            AuthScreen(modifier,navController)
        }

        composable("login"){
            LoginScreen(modifier,navController)
        }

        composable("signup"){
            SignupScreen(modifier,navController)
        }

        composable("home"){
            HomeScreen(modifier,navController)
        }

        composable("category-products/{categoryId}"){
            val categoryId = it.arguments?.getString("categoryId")
            CategoryProductPage(modifier,categoryId?:"")
        }

        composable("product-details/{productId}"){
            val productId = it.arguments?.getString("productId")
            ProductDetailsPage(modifier,productId?:"")
        }

        composable("checkout"){
            CheckoutPage(modifier)
        }

        composable("orders"){
            OrderPages(modifier)
        }


        composable("search") {
            SearchPage(
                onBackClick = { navController.popBackStack() },
                onProductClick = { productId ->
                    navController.navigate("product-details/$productId")
                }
            )
        }

        composable("setting"){
            Setting(modifier)
        }

        composable("help"){
            Help(modifier)
        }
    }
}

object GlobalNavigation{
    lateinit var navController: NavHostController
}
