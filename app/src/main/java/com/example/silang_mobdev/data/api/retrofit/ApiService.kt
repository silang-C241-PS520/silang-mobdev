package com.example.silang_mobdev.data.api.retrofit



import com.example.silang_mobdev.data.api.request.LoginRequest
import com.example.silang_mobdev.data.api.request.RegisterRequest
import com.example.silang_mobdev.data.api.response.LoginResponse
import com.example.silang_mobdev.data.api.response.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

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
    suspend fun logout()
}