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

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        apiService.logout()
        userPreference.logout()
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
