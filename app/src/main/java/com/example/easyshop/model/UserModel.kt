package com.example.easyshop.model

data class UserModel (
    val name : String,
    val email : String,
    val uid : String,
    val cartItem : Map<String,Long> = emptyMap()
)