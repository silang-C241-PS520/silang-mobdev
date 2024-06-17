package com.example.silang_mobdev.ui.translate

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.silang_mobdev.R
import com.example.silang_mobdev.ViewModelFactory
import com.example.silang_mobdev.databinding.ActivityTranslateBinding
import com.example.silang_mobdev.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class TranslateActivity : AppCompatActivity() {

    private val viewModel by viewModels<TranslateViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityTranslateBinding
    private lateinit var videoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTranslateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Retrieve the video URI passed from MainActivity
        videoUri = Uri.parse(intent.getStringExtra("videoUri") ?: "")

        // Get video metadata and display it
        displayVideoMetadata()
        displayThumbnail()
        uploadVideo()
        videoResult()
    }

    @SuppressLint("DefaultLocale")
    private fun displayVideoMetadata() {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(this, videoUri)

        // Get video duration in milliseconds
        val durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val duration = durationString?.toLongOrNull() ?: 0
        val durationFormatted = String.format(
            "%02d:%02d:%02d",
            duration / 3600000,
            (duration % 3600000) / 60000,
            (duration % 60000) / 1000
        )

        // Get video name
        val videoName = videoUri.lastPathSegment ?: ""

        // Get video size
        val videoSizeBytes =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toLongOrNull()
                ?: 0
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

    // Function to upload the video
    private fun uploadVideo() {
        videoUri.let { uri ->

            val videoFile = uriToFile(uri, this)

            val requestVideoFile = videoFile.asRequestBody("video/mp4".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "file",
                videoFile.name,
                requestVideoFile
            )

            showLoading(true)
            viewModel.uploadVideo(multipartBody)
        }
    }

    private fun videoResult() {
        viewModel.uploadVideoResult.observe(this) { result ->
            showLoading(false)
            if (result != null) {
                binding.resultCardView.visibility = View.VISIBLE
                binding.textResult.text = result.translation_text
                showToast(getString(R.string.upload_success))
            } else {
                binding.resultCardView.visibility = View.GONE
                showToast(getString(R.string.upload_failed))
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
