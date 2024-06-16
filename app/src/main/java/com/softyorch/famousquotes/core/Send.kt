package com.softyorch.famousquotes.core

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.ui.utils.extFunc.getResourceString
import com.softyorch.famousquotes.utils.writeLog
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject


class Send @Inject constructor(@ApplicationContext private val context: Context) {

    private val name = context.getResourceString(BuildConfig.APP_TITLE)
    private val pkgName = BuildConfig.DB_COLLECTION.replace("_quotes", "")

    fun sendDataTo(data: String) {
        val htmlFormattedMessage =
            "https://play.google.com/store/apps/details?id=com.softyorch.famousquotes.$pkgName"
        val sendText = "$data\n\n$name\n\n$htmlFormattedMessage"

        context.startActivity(
            Intent.createChooser(
                Intent(
                    Intent.ACTION_SEND
                ).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, sendText)
                }, "Today quote"
            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    fun sendImageAndQuoteTo(data: String, image: Bitmap) {

        resizeImage(image)?.let { resizeImage ->
            writeLog(text = "[Send] -> sendImageAndQuoteTo image: $image")
            val htmlFormattedMessage =
                "https://play.google.com/store/apps/details?id=com.softyorch.famousquotes.$pkgName"
            val sendText = "$data\n\n$name\n\n$htmlFormattedMessage"

            context.startActivity(
                Intent.createChooser(
                    Intent(
                        Intent.ACTION_SEND
                    ).apply {
                        type = "*/*"
                        putExtra(Intent.EXTRA_STREAM, resizeImage)
                        putExtra(Intent.EXTRA_TEXT, sendText)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }, "Today quote"
                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }.let {
            writeLog(text = "[Send] -> sendImageAndQuoteTo null")
        }
    }

    private fun resizeImage(image: Bitmap): Uri? = try {

        val scaledBitmap = Bitmap.createScaledBitmap(image, 512, 896, true)

        // Generate a unique filename
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val filename = "image_$timestamp.jpg"

        val imagesDir = File(context.cacheDir, "images")
        imagesDir.mkdirs()

        val file = File(imagesDir, filename)

        FileOutputStream(file).use { outputStream ->
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }

        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    } catch (ex: Exception) {
        null // Return null to indicate failure
    }

}
