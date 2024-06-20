import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.silang_mobdev.data.Repository
import com.example.silang_mobdev.data.api.response.MeResponse
import com.example.silang_mobdev.data.api.response.TranslationResponse
import com.example.silang_mobdev.data.api.retrofit.ApiConfig
import com.example.silang_mobdev.data.pref.UserModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainViewModel(private val repository: Repository) : ViewModel() {

    private val _meLiveData = MutableLiveData<MeResponse>()
    val meLiveData: LiveData<MeResponse>
        get() = _meLiveData

    private val _historyLiveData = MutableLiveData<List<TranslationResponse>>()
    val historyLiveData: LiveData<List<TranslationResponse>>
        get() = _historyLiveData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getCurrentUser() {
        viewModelScope.launch {
            try {
                val user = repository.getSession().first()
                Log.e("USER", "$user")
                if (user.token.isNotEmpty()) {
                    val apiService = ApiConfig.getApiService(user.token)
                    val meResponse = apiService.me()
                    _meLiveData.postValue(meResponse)
                } else {
                    _errorMessage.postValue("Token not found, please login again.")
                }
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    // Handle unauthorized error by logging out
                    repository.logout()
                    _errorMessage.postValue("Unauthorized access. Logging out.")
                } else {
                    // Handle other HTTP exceptions
                    _errorMessage.postValue("HttpException: ${e.message()}")
                }
            } catch (e: Exception) {
                // Handle other exceptions
                _errorMessage.postValue("Exception: ${e.message}")
            }
        }
    }

    fun getRecentUserHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = repository.getSession().first()
                if (user.token.isNotEmpty()) {
                    val apiService = ApiConfig.getApiService(user.token)
                    val historyResponse = apiService.currentUserHistory()
                    val recentHistory = historyResponse.take(5)
                    _historyLiveData.postValue(recentHistory)
                    _isLoading.value = false
                } else {
                    _isLoading.value = false
                    _errorMessage.postValue("Token not found, please login again.")
                }
            } catch (e: HttpException) {
                _isLoading.value = false
                // Handle HTTP exceptions
                Log.e("MainViewModel", "HttpException: ${e.message()}")
                _errorMessage.postValue("HttpException: ${e.message()}")
            } catch (e: Exception) {
                _isLoading.value = false
                // Handle other exceptions
                Log.e("MainViewModel", "Exception: ${e.message}")
                _errorMessage.postValue("Exception: ${e.message}")
            }
        }
    }

    fun getSession(): LiveData<UserModel> {
        getCurrentUser()
        getRecentUserHistory()
        return repository.getSession().asLiveData()
    }
}

