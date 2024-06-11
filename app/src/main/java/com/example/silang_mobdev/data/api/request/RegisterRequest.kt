package com.example.silang_mobdev.data.api.request

data class RegisterRequest(
	val username: String? = null,
	val password: String? = null,
	val confirm_password: String? = null
)
