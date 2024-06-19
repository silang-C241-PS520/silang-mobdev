package com.example.silang_mobdev.data.api.retrofit


import com.example.silang_mobdev.data.api.request.FeedbackRequest
import com.example.silang_mobdev.data.api.request.RegisterRequest
import com.example.silang_mobdev.data.api.response.LoginResponse
import com.example.silang_mobdev.data.api.response.MeResponse
import com.example.silang_mobdev.data.api.response.RegisterResponse
import com.example.silang_mobdev.data.api.response.TranslationResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @POST("/api/v1/auth/register")
    suspend fun register1(
        @Body registerResponse: RegisterRequest
    ): RegisterResponse


    @FormUrlEncoded
    @POST("/api/v1/auth/login")
    suspend fun login1(
        @Field("username") username: String,
        @Field("password") password: String
    ): LoginResponse

    @POST("/api/v1/auth/logout")
    suspend fun logout(): Response<Void>

    @GET("/api/v1/auth/me")
    suspend fun me(): MeResponse

    @Multipart
    @POST("/api/v1/translations/")
    suspend fun uploadVideo(
        @Part file: MultipartBody.Part,
    ): TranslationResponse

    @GET("/api/v1/translations/me")
    suspend fun currentUserHistory(): List<TranslationResponse>

    @PUT("/api/v1/translations/{id}/feedbacks")
    suspend fun updateFeedback(
        @Path("id") id: Int?,
        @Body feedbackResponse: FeedbackRequest
    ): TranslationResponse

}