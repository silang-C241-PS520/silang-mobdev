package com.example.silang_mobdev.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.silang_mobdev.R
import com.example.silang_mobdev.ViewModelFactory
import com.example.silang_mobdev.databinding.ActivityProfileBinding
import com.example.silang_mobdev.databinding.ActivityTranslateBinding

class ProfileActivity : AppCompatActivity() {

    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.getCurrentUser()

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.logout.setOnClickListener {
            viewModel.logout()
        }

        observeMe()
    }

    private fun observeMe() {
        viewModel.meLiveData.observe(this) { meResponse ->
            Log.d("MainActivity", "Received story response: $meResponse")
            binding.tvProfileName.text = meResponse.username
        }
    }


}