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
    }
}

object GlobalNavigation{
    lateinit var navController: NavHostController
}
