package com.example.ticketsaplication.ui

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun createImageFile(context: Context): Pair<Uri, File> {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", context.cacheDir)
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    return Pair(uri, file)
}

fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("upload_${System.currentTimeMillis()}", ".jpg", context.cacheDir)
        tempFile.outputStream().use { inputStream.copyTo(it) }
        tempFile
    } catch (e: Exception) { null }
}
