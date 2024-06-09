package com.example.silang_mobdev

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.silang_mobdev.databinding.ActivityMainBinding
import com.example.silang_mobdev.ui.history.HistoryActivity
import com.example.silang_mobdev.ui.login.LoginActivity
import com.example.silang_mobdev.ui.translate.TranslateActivity
import com.example.silang_mobdev.utils.getVideoUri
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private var currentVideoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        binding.exitIcon.setOnClickListener {
            viewModel.logout()
        }

        binding.galleryCardView.setOnClickListener {
            startGallery()
        }

        binding.cameraCardView.setOnClickListener {
            startCamera()
        }

        binding.seeAllHistory.setOnClickListener {
            val intent = Intent(this@MainActivity, HistoryActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
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

}