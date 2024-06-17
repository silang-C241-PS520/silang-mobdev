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

    fun getCurrentUser() {
        viewModelScope.launch {
            try {
                val user = repository.getSession().first()
                val apiService = ApiConfig.getApiService(user.token)
                val meResponse = apiService.me()
                _meLiveData.postValue(meResponse)
            } catch (e: HttpException) {
                // Handle HTTP exceptions
                Log.e("MainViewModel", "HttpException: ${e.message()}")
            } catch (e: Exception) {
                // Handle other exceptions
                Log.e("MainViewModel", "Exception: ${e.message}")
            }
        }
    }

    fun getRecentUserHistory() {
        viewModelScope.launch {
            try {
                val user = repository.getSession().first()
                val apiService = ApiConfig.getApiService(user.token)
                val historyResponse = apiService.currentUserHistory()
                // Take only the 5 most recent items
                val recentHistory = historyResponse.take(8)
                _historyLiveData.postValue(recentHistory)
            } catch (e: HttpException) {
                // Handle HTTP exceptions
                Log.e("MainViewModel", "HttpException: ${e.message()}")
            } catch (e: Exception) {
                // Handle other exceptions
                Log.e("MainViewModel", "Exception: ${e.message}")
            }
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}
