package com.example.silang_mobdev.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import de.hdodenhof.circleimageview.BuildConfig
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val MAX_VIDEO_SIZE_MB = 0.5 //1 MB
private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

fun getVideoUri(context: Context): Uri {
    var uri: Uri? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.mp4")
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Movies/MyCamera/")
        }
        uri = context.contentResolver.insert(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }
    return uri ?: getVideoUriForPreQ(context)
}

private fun getVideoUriForPreQ(context: Context): Uri {
    val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
    val videoFile = File(filesDir, "/MyCamera/$timeStamp.mp4")
    if (videoFile.parentFile?.exists() == false) videoFile.parentFile?.mkdir()
    return FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.fileprovider",
        videoFile
    )
}

fun createCustomTempVideoFile(context: Context): File {
    val filesDir = context.externalCacheDir
    return File.createTempFile(timeStamp, ".mp4", filesDir)
}

fun uriToFile(videoUri: Uri, context: Context): File {
    val myFile = createCustomTempVideoFile(context)
    val inputStream = context.contentResolver.openInputStream(videoUri) as InputStream
    val outputStream = FileOutputStream(myFile)
    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
    outputStream.close()
    inputStream.close()
    return myFile
}

//fun File.reduceFileVideo(): File {
//    val inputFile = this
//    val outputFile = File(inputFile.parent, "compressed_${inputFile.name}")
//
//    try {
//        // Compress the video using FFmpeg
//        val command = arrayOf(
//            "-i", inputFile.path,
//            "-vf", "scale=640:-2", // Resize video to width 640px, keep aspect ratio
//            "-b:v", "1M", // Set video bitrate to 1M
//            "-c:a", "copy", // Copy audio codec
//            outputFile.path
//        )
//
//        val rc = FFmpeg.execute(command)
//        if (rc != 0) {
//            Log.e("FFmpeg", "Compression failed with return code $rc.")
//        }
//
//        // Check the size of the compressed video
//        if (outputFile.length() > MAX_VIDEO_SIZE_MB * 1024 * 1024) {
//            Log.e("VideoCompression", "Compressed video is still too large.")
//        } else {
//            Log.i("VideoCompression", "Video compressed successfully.")
//        }
//    } catch (e: Exception) {
//        Log.e("VideoCompression", "Compression failed.", e)
//    }
//
//    return outputFile
//}
