package com.softyorch.famousquotes.core

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.FileProvider
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.ui.utils.extFunc.getResourceString
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class Send @Inject constructor(@ApplicationContext private val context: Context) {

    private val name = context.getResourceString(BuildConfig.APP_TITLE)
    private val pkgName = BuildConfig.DB_COLLECTION.replace("_quotes", "")

    suspend fun sendDataTo(data: String, imageUri: String) {
        val htmlFormattedMessage =
            "https://play.google.com/store/apps/details?id=com.softyorch.famousquotes.$pkgName"
        val sendText = "$data\n\n$name\n\n$htmlFormattedMessage"

        downloadImageAndSaveToTempFile(imageUri, sendText)?.let { uri ->
            context.startActivity(
                Intent(Intent.ACTION_SEND).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            )
        }
    }

    private suspend fun downloadImageAndSaveToTempFile(imageUrl: String, textToDraw: String): Uri? {
        val imageLoader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .build()

        return withContext(Dispatchers.IO) {
            val result = (imageLoader.execute(request) as? SuccessResult)?.drawable
            if (result is BitmapDrawable) {
                // Crea una copia mutable del Bitmap
                val mutableBitmap = result.bitmap.copy(Bitmap.Config.ARGB_8888, true)

                // Crea un Canvas para dibujar en el Bitmap mutable
                val canvas = Canvas(mutableBitmap)

                // Configura el Paint para el texto
                val paint = Paint().apply {
                    color = Color.White.toArgb() // Color del texto
                    textSize = 30f // Tamaño del texto
                    textAlign = Paint.Align.CENTER // Alineación del texto
                }

                // Dibuja el texto en el Canvas
                val x = mutableBitmap.width / 2f // Posición horizontal (centrado)
                val y = mutableBitmap.height / 2f // Posición vertical (centrado)
                canvas.drawText(textToDraw, x, y, paint)

                // Guarda el Bitmap modificado en un archivo temporal
                val cacheDir = context.externalCacheDir
                val file = File(cacheDir, "temp_image.png")
                FileOutputStream(file).use { outputStream ->
                    mutableBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }

                // Devuelve el Uri del archivo temporal
                FileProvider.getUriForFile(
                    context,
                    "com.softyorch.famousquotes.uplifting.dev.fileprovider",
                    file
                )
            } else {
                null
            }
        }
    }

}
