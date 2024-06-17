package com.example.silang_mobdev.data.api.response

import com.google.gson.annotations.SerializedName

data class TranslationResponse(
	val id: Int,
	val video_url: String,
	val translation_text: String,
	val date_time_created: String,
	val feedback: String?
)

