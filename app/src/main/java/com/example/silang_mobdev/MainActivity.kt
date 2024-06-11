package com.example.silang_mobdev

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.silang_mobdev.databinding.ActivityMainBinding
import com.example.silang_mobdev.ui.history.HistoryActivity
import com.example.silang_mobdev.ui.login.LoginActivity
import com.example.silang_mobdev.ui.translate.TranslateActivity
import com.example.silang_mobdev.ui.profile.ProfileActivity

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

        binding.galleryCardView.setOnClickListener {
            startGallery()
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

}