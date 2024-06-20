package com.example.silang_mobdev

import HistoryAdapter
import MainViewModel
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.silang_mobdev.databinding.ActivityMainBinding
import com.example.silang_mobdev.ui.history.HistoryActivity
import com.example.silang_mobdev.ui.login.LoginActivity
import com.example.silang_mobdev.ui.translate.TranslateActivity
import com.example.silang_mobdev.ui.profile.ProfileActivity
import com.example.silang_mobdev.utils.getVideoUri

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var currentVideoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()


        historyAdapter = HistoryAdapter()

        val recyclerView = findViewById<RecyclerView>(R.id.rv_history)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.suppressLayout(true)
        recyclerView.adapter = historyAdapter

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.getRecentUserHistory()
        }

        viewModel.getSession().observe(this) { user ->
            observeMe()
            observeCurrentHistory()
            if (!user.isLogin) {
                viewModel.errorMessage.observe(this) {
                    showToast(it)
                }
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        binding.galleryCardView.setOnClickListener {
            startGallery()
        }

        binding.cameraCardView.setOnClickListener {
            showToast("Camera Feature is Under Maintenance")
        }

        binding.profile.setOnClickListener {
            val intent = Intent(this@MainActivity, ProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.seeAllHistory.setOnClickListener {
            val intent = Intent(this@MainActivity, HistoryActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

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
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            if (getFileSize(uri) <= 3 * 1024 * 1024) { // 5 MB in bytes
                currentVideoUri = uri
                val intent = Intent(this@MainActivity, TranslateActivity::class.java)
                intent.putExtra("videoUri", uri.toString())
                startActivity(intent)
            } else {
                Log.d("Video Picker", "Selected video exceeds 5 MB")
                Toast.makeText(this, R.string.video_too_large, Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d("Video Picker", "No media selected")
        }
    }

    private fun getFileSize(uri: Uri): Long {
        var fileSize: Long = 0
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
            it.moveToFirst()
            fileSize = it.getLong(sizeIndex)
        }
        return fileSize
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