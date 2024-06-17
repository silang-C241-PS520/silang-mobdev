package com.example.silang_mobdev.data.api.response

import com.google.gson.annotations.SerializedName

data class MeResponse(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("username")
	val username: String? = null
)
