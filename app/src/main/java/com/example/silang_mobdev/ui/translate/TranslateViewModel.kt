package com.example.silang_mobdev.ui.translate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.silang_mobdev.data.Repository
import com.example.silang_mobdev.data.api.response.TranslationResponse
import com.example.silang_mobdev.data.api.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class TranslateViewModel(private val repository: Repository) : ViewModel() {

    private val _uploadVideoResult = MutableLiveData<TranslationResponse>()
    val uploadVideoResult: LiveData<TranslationResponse>
        get() = _uploadVideoResult

    fun uploadVideo(video: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                // Assuming you have a repository function to handle the video upload
                val user = repository.getSession().first()
                val response = repository.uploadVideo(video, user.token)
                _uploadVideoResult.postValue(response)
            } catch (e: Exception) {
                ///
            }
        }
    }
}