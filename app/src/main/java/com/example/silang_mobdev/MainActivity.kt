package com.example.silang_mobdev

import HistoryAdapter
import MainViewModel
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.silang_mobdev.data.api.retrofit.ApiConfig
import com.example.silang_mobdev.data.pref.UserModel
import com.example.silang_mobdev.databinding.ActivityMainBinding
import com.example.silang_mobdev.ui.history.HistoryActivity
import com.example.silang_mobdev.ui.login.LoginActivity
import com.example.silang_mobdev.ui.translate.TranslateActivity
import com.example.silang_mobdev.ui.profile.ProfileActivity
import com.example.silang_mobdev.utils.getVideoUri
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var historyAdapter: HistoryAdapter
    private var currentVideoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.getCurrentUser()
        viewModel.getRecentUserHistory()

        supportActionBar?.hide()


        historyAdapter = HistoryAdapter()

        val recyclerView = findViewById<RecyclerView>(R.id.rv_history)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = historyAdapter


        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        binding.galleryCardView.setOnClickListener {
            startGallery()
        }

        binding.cameraCardView.setOnClickListener {
            startCamera()
        }

        binding.profileIcon.setOnClickListener {
            val intent = Intent(this@MainActivity, ProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }


        binding.seeAllHistory.setOnClickListener {
            val intent = Intent(this@MainActivity, HistoryActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        observeMe()
        observeCurrentHistory()
    }


    private fun observeMe() {
        viewModel.meLiveData.observe(this) { meResponse ->
            Log.d("MainActivity", "Received story response: $meResponse")
            binding.yourName.text = meResponse.username
        }
    }

    private fun observeCurrentHistory() {
        viewModel.historyLiveData.observe(this) { historyList ->
            historyAdapter.submitList(historyList)
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentVideoUri = uri
            val intent = Intent(this@MainActivity, TranslateActivity::class.java)
            intent.putExtra("videoUri", uri.toString())
            startActivity(intent)
        } else {
            Log.d("Video Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentVideoUri = getVideoUri(this)
        Log.d("Messages", "$currentVideoUri")
        launcherIntentCamera.launch(currentVideoUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.CaptureVideo()
    ) { isSuccess ->
        if (isSuccess) {
            Log.d("Messages", "$isSuccess")
        } else {
            Log.e("Video Recording", "Video recording failed or was canceled $isSuccess")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}