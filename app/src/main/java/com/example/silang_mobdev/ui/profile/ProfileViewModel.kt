package com.example.silang_mobdev.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.silang_mobdev.data.Repository
import com.example.silang_mobdev.data.api.response.MeResponse
import com.example.silang_mobdev.data.api.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ProfileViewModel(private val repository: Repository) : ViewModel() {
    private val _meLiveData = MutableLiveData<MeResponse>()
    val meLiveData: LiveData<MeResponse>
        get() = _meLiveData

    private val _isLoggedOut = MutableLiveData<Boolean>()
    val isLoggedOut: LiveData<Boolean> = _isLoggedOut

    fun getCurrentUser() {
        viewModelScope.launch {
            try {
                val user = repository.getSession().first()
                val apiService = ApiConfig.getApiService(user.token)
                val meResponse = apiService.me()
                _meLiveData.postValue(meResponse)
            } catch (e: HttpException) {
                // Handle HTTP exceptions
                Log.e("ProfileViewModel", "HttpException: ${e.message()}")
            } catch (e: Exception) {
                // Handle other exceptions
                Log.e("ProfileViewModel", "Exception: ${e.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                repository.logout()
                _isLoggedOut.postValue(true)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Exception during logout: ${e.message}")
            }
        }
    }
}
