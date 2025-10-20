package com.example.easyshop.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.easyshop.AppUtil
import com.example.easyshop.R
import com.example.easyshop.viewmodel.AuthViewModel
import kotlin.Result.Companion.success

@Composable
fun SignupScreen(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel = viewModel()) {

    var email by remember{
        mutableStateOf("")
    }

    var name by remember{
        mutableStateOf("")
    }

    var password by remember{
        mutableStateOf("")
    }

    var isLoading by remember{
        mutableStateOf(false)
    }

    val context = LocalContext.current

    Column(
        modifier= Modifier.fillMaxSize()
            .padding(32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Text("Hello there!",
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight  = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            )
        )
        Spacer(modifier.height(10.dp))

        Text("Create an account",
            style = TextStyle(
                fontSize = 22.sp
            )
        )
        Spacer(modifier.height(10.dp))

        Image(
            painter = painterResource(id = R.drawable.banner_user),
            contentDescription = "Banner_user",
            modifier = Modifier.fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier.height(20.dp))

        OutlinedTextField(
            value = email ,onValueChange ={
            email = it
        },
            label = {
                Text("Email Address")
            },
            modifier = Modifier.fillMaxWidth()
            )

        Spacer(modifier.height(10.dp))

        OutlinedTextField(
            value = name ,onValueChange ={
                name = it
            },
            label = {
                Text("Full Name")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier.height(10.dp))

        OutlinedTextField(
            value = password ,onValueChange ={
                password = it
            },
            label = {
                Text("Password")
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            isLoading = true
            authViewModel.signup(email,name,password){success,errorMessage ->
                if(success){
                    isLoading = false
                    navController.navigate("home")
                    {
                        popUpTo("auth"){inclusive = true}
                    }
                }else{
                    isLoading = false
                    AppUtil.showToast(context,errorMessage?:"SomeThing Went Wrong")
                }
            }
        },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text(if(isLoading) "Loading..." else "SignUp", fontSize = 22.sp)
        }
    }
}