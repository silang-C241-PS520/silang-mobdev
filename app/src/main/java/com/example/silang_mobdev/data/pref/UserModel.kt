package com.example.silang_mobdev.data.pref


data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false,
    val tokenExpirationTime: Long = 0L
)
