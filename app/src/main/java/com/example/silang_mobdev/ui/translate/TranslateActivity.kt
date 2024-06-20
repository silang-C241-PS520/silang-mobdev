package com.example.silang_mobdev.ui.translate

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.silang_mobdev.MainActivity
import com.example.silang_mobdev.R
import com.example.silang_mobdev.ViewModelFactory
import com.example.silang_mobdev.data.api.request.FeedbackRequest
import com.example.silang_mobdev.data.api.retrofit.ApiConfig
import com.example.silang_mobdev.databinding.ActivityTranslateBinding
import com.example.silang_mobdev.utils.reduceFileVideo
import com.example.silang_mobdev.utils.uriToFile
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class TranslateActivity : AppCompatActivity() {

    private val viewModel by viewModels<TranslateViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityTranslateBinding
    private lateinit var videoUri: Uri
    private var uploadResultId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTranslateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.translate.setOnClickListener { uploadVideo() }
        binding.feedback.setOnClickListener { showFeedbackDialog(uploadResultId) }

        // Retrieve the video URI passed from MainActivity
        videoUri = Uri.parse(intent.getStringExtra("videoUri") ?: "")

        // Get video metadata and display it
        displayVideoMetadata()
        displayThumbnail()
        observeUploadResult()
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
        "$videoSizeMb MB".also { binding.videoSizeTextView.text = it }

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

    private fun uploadVideo() {
        showLoading(true)
        videoUri.let { uri ->
            val videoFile = uriToFile(uri, this).reduceFileVideo()
            val requestVideoFile = videoFile.asRequestBody("video/mp4".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "file",
                videoFile.name,
                requestVideoFile
            )
            viewModel.uploadVideo(multipartBody)
        }
    }


    private fun observeUploadResult() {

        viewModel.uploadVideoResult.observe(this) { result ->
            showLoading(false)
            if (result != null) {
                binding.resultCardView.visibility = View.VISIBLE
                binding.textResult.text = result.translation_text
                uploadResultId = result.id
                showToast(getString(R.string.upload_success))
            } else {
                binding.resultCardView.visibility = View.GONE
                showToast(getString(R.string.upload_failed))
            }
        }

        viewModel.uploadError.observe(this) { isError ->
            if (isError) {
                showLoading(false)
                viewModel.uploadErrorMssg.observe(this) { errorMsg ->
                    showToast(errorMsg)
                }
            }
        }
    }

    private fun showFeedbackDialog(resultId: Int?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_feedback, null)
        val editTextFeedback = dialogView.findViewById<EditText>(R.id.feedbackEditText)
        val buttonSubmit = dialogView.findViewById<Button>(R.id.submitFeedbackButton)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle(getString(R.string.feedback_title))
            .create()

        buttonSubmit.setOnClickListener {
            val feedback = editTextFeedback.text.toString().trim()
            if (feedback.isNotEmpty()) {
                viewModel.submitFeedback(resultId, feedback)
                showToast("Feedback is submitted")
                dialog.dismiss()
            } else {
                showToast("Please enter your feedback")
            }
        }

        dialog.show()
    }

    fun copyTextToClipboard(view: View) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Result Text", binding.textResult.text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }

}
