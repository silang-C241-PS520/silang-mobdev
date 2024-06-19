package com.example.silang_mobdev.data


import android.util.Log
import com.example.silang_mobdev.data.api.response.TranslationResponse
import com.example.silang_mobdev.data.api.retrofit.ApiConfig
import com.example.silang_mobdev.data.api.retrofit.ApiService
import com.example.silang_mobdev.data.pref.UserModel
import com.example.silang_mobdev.data.pref.UserPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class Repository private constructor(
    private var apiService: ApiService,
    private val userPreference: UserPreference
) {

    init {
        runBlocking {
            val user = userPreference.getUser().first()
            if (user.token.isNotEmpty()) {
                apiService = ApiConfig.getApiService(user.token)
            }
        }
    }

    suspend fun uploadVideo(video: MultipartBody.Part): TranslationResponse {
        return apiService.uploadVideo(video)
    }
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        try {
            val response = apiService.logout()
            if (response.isSuccessful) {
                userPreference.logout()
            } else {
                // Handle the case where the API call is not successful
                Log.e("Logout", "API logout failed: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            // Handle the exception, log it, or notify the user
            Log.e("Logout", "Logout failed", e)
        }
    }


    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        fun getInstance(apiService: ApiService, userPreference: UserPreference): Repository {
            return INSTANCE ?: synchronized(this) {
                val instance = Repository(apiService, userPreference)
                INSTANCE = instance
                instance
            }
        }
    }
}
