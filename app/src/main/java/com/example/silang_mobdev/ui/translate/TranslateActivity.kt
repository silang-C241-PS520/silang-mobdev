package com.example.silang_mobdev.ui.translate

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.ui.AppBarConfiguration
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.silang_mobdev.R
import com.example.silang_mobdev.databinding.ActivityMainBinding
import com.example.silang_mobdev.databinding.ActivityTranslateBinding

class TranslateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTranslateBinding
    private lateinit var videoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTranslateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the video URI passed from MainActivity
        videoUri = Uri.parse(intent.getStringExtra("videoUri") ?: "")

        // Get video metadata and display it
        displayVideoMetadata()

        displayThumbnail()
    }

    @SuppressLint("DefaultLocale")
    private fun displayVideoMetadata() {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(this, videoUri)

        // Get video duration in milliseconds
        val durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val duration = durationString?.toLongOrNull() ?: 0
        val durationFormatted = String.format("%02d:%02d:%02d",
            duration / 3600000,
            (duration % 3600000) / 60000,
            (duration % 60000) / 1000
        )

        // Get video name
        val videoName = videoUri.lastPathSegment ?: ""

        // Get video size
        val videoSizeBytes = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toLongOrNull() ?: 0
        val videoSizeMb = videoSizeBytes / (1024 * 1024)

        binding.videoNameTextView.text = videoName
        binding.videoDurationTextView.text = durationFormatted
        binding.videoSizeTextView.text = "$videoSizeMb MB"

        retriever.release()
    }

    private fun displayThumbnail() {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(this, videoUri)

        // Get thumbnail bitmap
        val bitmap: Bitmap? = retriever.getFrameAtTime(0)

        // Display thumbnail in ImageView
        Glide.with(binding.videoThumbnailImageView.context)
            .load(bitmap)
            .transform(RoundedCorners(100))
            .into(binding.videoThumbnailImageView)

        retriever.release()
    }
}