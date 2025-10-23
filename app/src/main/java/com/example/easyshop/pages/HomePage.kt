package com.example.easyshop.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.easyshop.components.BannerView
import com.example.easyshop.components.HeaderView

@Composable
fun HomePage(modifier: Modifier = Modifier) {

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp)
        .verticalScroll(rememberScrollState())
    ){
        HeaderView(modifier)
        BannerView(modifier = Modifier.height(200.dp))
    }
    
}