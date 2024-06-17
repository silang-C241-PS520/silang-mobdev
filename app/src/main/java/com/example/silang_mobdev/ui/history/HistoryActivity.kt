package com.example.silang_mobdev.ui.history

import HistoryAdapter
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.silang_mobdev.R
import com.example.silang_mobdev.ViewModelFactory
import com.example.silang_mobdev.databinding.ActivityHistoryBinding
import com.example.silang_mobdev.databinding.ActivityTranslateBinding

class HistoryActivity : AppCompatActivity() {
    private val viewModel by viewModels<HistoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.getUserHistory()

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        historyAdapter = HistoryAdapter()

        val recyclerView = findViewById<RecyclerView>(R.id.rv_history)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = historyAdapter

        viewModel.historyLiveData.observe(this) { historyList ->
            historyAdapter.submitList(historyList)
        }
    }

}