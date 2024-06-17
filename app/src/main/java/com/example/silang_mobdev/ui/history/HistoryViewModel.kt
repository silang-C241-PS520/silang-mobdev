package com.example.silang_mobdev.ui.history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.silang_mobdev.data.Repository
import com.example.silang_mobdev.data.api.response.TranslationResponse
import com.example.silang_mobdev.data.api.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HistoryViewModel(private val repository: Repository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    private val _historyLiveData = MutableLiveData<List<TranslationResponse>>()
    val historyLiveData: LiveData<List<TranslationResponse>>
        get() = _historyLiveData

    fun getUserHistory() {
        viewModelScope.launch {
            try {
                val user = repository.getSession().first()
                val apiService = ApiConfig.getApiService(user.token)
                val historyResponse = apiService.currentUserHistory()
                // Take only the 5 most recent items
                _historyLiveData.postValue(historyResponse)
            } catch (e: HttpException) {
                // Handle HTTP exceptions
                Log.e("MainViewModel", "HttpException: ${e.message()}")
            } catch (e: Exception) {
                // Handle other exceptions
                Log.e("MainViewModel", "Exception: ${e.message}")
            }
        }
    }
}