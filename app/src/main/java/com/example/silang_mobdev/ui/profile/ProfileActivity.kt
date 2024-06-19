package com.example.silang_mobdev.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.silang_mobdev.R
import com.example.silang_mobdev.ViewModelFactory
import com.example.silang_mobdev.databinding.ActivityProfileBinding
import com.example.silang_mobdev.ui.login.LoginActivity

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
            observeLogout()// Call the logout function
        }

        observeMe()
    }

    private fun observeMe() {
        viewModel.meLiveData.observe(this) { meResponse ->
            Log.d("ProfileActivity", "Received user response: $meResponse")
            binding.tvProfileName.text = meResponse.username
        }
    }

    private fun observeLogout() {
        viewModel.isLoggedOut.observe(this) { isLoggedOut ->
            if (isLoggedOut) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}
