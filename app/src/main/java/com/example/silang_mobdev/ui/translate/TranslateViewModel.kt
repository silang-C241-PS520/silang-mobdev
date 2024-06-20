package com.example.silang_mobdev.ui.translate

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.silang_mobdev.data.Repository
import com.example.silang_mobdev.data.api.request.FeedbackRequest
import com.example.silang_mobdev.data.api.response.TranslationResponse
import com.example.silang_mobdev.data.api.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class TranslateViewModel(private val repository: Repository) : ViewModel() {

    private val _uploadVideoResult = MutableLiveData<TranslationResponse>()
    val uploadVideoResult: LiveData<TranslationResponse>
        get() = _uploadVideoResult

    private val _uploadError = MutableLiveData<Boolean>()
    val uploadError: LiveData<Boolean>
        get() = _uploadError


    private val _feedbackSubmitted = MutableLiveData<Boolean>()
    val feedbackSubmitted: LiveData<Boolean>
        get() = _feedbackSubmitted

    private val _uploadErrorMssg = MutableLiveData<String>()
    val uploadErrorMssg: LiveData<String>
        get() = _uploadErrorMssg

    fun uploadVideo(video: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                // Assuming you have a repository function to handle the video upload
                val user = repository.getSession().first()
                val apiService = ApiConfig.getApiService(user.token)
                val response = apiService.uploadVideo(video)
                _uploadVideoResult.postValue(response)
            } catch (e: Exception) {
                _uploadError.postValue(true) // Notify activity about the upload error
                _uploadErrorMssg.postValue(e.message ?: "Unknown error")
            }
        }
    }


    fun submitFeedback(resultId: Int?, feedback: String) {
        viewModelScope.launch {
            try {
                val user = repository.getSession().first()
                val apiService = ApiConfig.getApiService(user.token)
                val response = apiService.updateFeedback(resultId, FeedbackRequest(feedback))
                Log.d("Feedback Response", "$response")
                _feedbackSubmitted.postValue(true)
            } catch (e: Exception) {
                _uploadError.postValue(true) // Notify the UI about the feedback submission error
            }
        }
    }
}