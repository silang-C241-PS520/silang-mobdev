package com.example.silang_mobdev.ui.history

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.silang_mobdev.R
import com.example.silang_mobdev.databinding.ActivityHistoryBinding
import com.example.silang_mobdev.databinding.ActivityTranslateBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

}