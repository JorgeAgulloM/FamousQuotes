package com.softyorch.famousquotes.core

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.FileProvider
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.theme.PrimaryColor
import com.softyorch.famousquotes.ui.theme.SecondaryColor
import com.softyorch.famousquotes.ui.theme.WhiteSmoke
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
        val subtitle = context.getResourceString("main_text_get_inspired")
        downloadImageAndSaveToTempFile(imageUri, name, data, subtitle)?.let { uri ->
            context.startActivity(
                Intent(Intent.ACTION_SEND).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_TEXT, "Mira esto! $htmlFormattedMessage")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            )
        }
    }

    private suspend fun downloadImageAndSaveToTempFile(
        imageUrl: String,
        titleToDraw: String,
        textToDraw: String,
        subtitle: String
    ): Uri? {
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
                var x = mutableBitmap.width / 3.4f // Posición horizontal (centrado)
                var y = mutableBitmap.height / 1.35f // Posición vertical (centrado)

                val iconBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.icon_quote)
                // Cambiar el color a rojo
                val paint = Paint()
                paint.colorFilter = PorterDuffColorFilter(SecondaryColor.toArgb(), PorterDuff.Mode.SRC_IN)

// Cambiar el tamaño
                val newWidth = 60
                val newHeight = 60
                val scaledIconBitmap = Bitmap.createScaledBitmap(iconBitmap, newWidth, newHeight, true)


/*// Dibujar el icono modificado
                canvas.drawBitmap(scaledIconBitmap, x, y, paint)
                canvas.restore()*/


                val iconWidth = scaledIconBitmap.width

                val textBounds = Rect()
                paint.getTextBounds(subtitle, 0, subtitle.length, textBounds)
                canvas.drawBitmap(scaledIconBitmap, x, y, paint)

                val titlePaint = Paint().apply {
                    color = WhiteSmoke.toArgb() // Color del texto
                    textSize = 60f // Tamaño del texto
                    textAlign = Paint.Align.CENTER // Alineación del texto
                    typeface = Typeface.DEFAULT_BOLD
                    setShadowLayer(10f, 2f, 2f, Color.Black.toArgb())
                }
                val textWidth = textBounds.width()
                val spacing = 10
                x = mutableBitmap.width / 1.8f
                val yTextCenter = y + scaledIconBitmap.height / 1.3f + textBounds.height() / 1.3f

                canvas.drawText(titleToDraw, x, yTextCenter, titlePaint)
                canvas.save()

/*                // Configura el título para el texto
                val titlePaint = Paint().apply {
                    color = WhiteSmoke.toArgb() // Color del texto
                    textSize = 60f // Tamaño del texto
                    textAlign = Paint.Align.CENTER // Alineación del texto
                    typeface = Typeface.DEFAULT_BOLD
                    setShadowLayer(10f, 2f, 2f, Color.Black.toArgb())
                }

                // Dibuja el texto en el Canvas
                x = mutableBitmap.width / 1.8f // Posición horizontal (centrado)
                y = mutableBitmap.height / 1.30f // Posición vertical (centrado)
                canvas.drawText(titleToDraw, x, y, titlePaint)*/

                // Configura el título para el texto
                val subtitlePaint = Paint().apply {
                    color = SecondaryColor.toArgb() // Color del texto
                    textSize = 60f // Tamaño del texto
                    textAlign = Paint.Align.CENTER // Alineación del texto
                    typeface = Typeface.DEFAULT_BOLD
                    setShadowLayer(10f, 2f, 2f, Color.Black.toArgb())
                }

                // Dibuja el texto en el Canvas
                x = mutableBitmap.width / 2f // Posición horizontal (centrado)
                y = mutableBitmap.height / 1.22f // Posición vertical (centrado)
                canvas.drawText(subtitle, x, y, subtitlePaint)


                val quotePaint = Paint().apply {
                    color = Color.White.toArgb() // Color del texto
                    textSize = 70f // Tamaño del texto
                    textAlign = Paint.Align.CENTER // Alineación del texto
                    typeface = Typeface.DEFAULT_BOLD
                    setShadowLayer(10f, 1f, 1f, Color.Black.toArgb())
                }

                val palabras = textToDraw.split(" ") // Dividir el texto en palabras

                var indice = 0
                var decrementY = 0.0f
                while (indice < palabras.size) {
                    val grupo = palabras.subList(indice, minOf(indice + 5, palabras.size))
                    x = mutableBitmap.width / 2f
                    y = mutableBitmap.height / (1.14f + decrementY)
                    canvas.drawText(grupo.joinToString(" "), x, y, quotePaint)
                    indice += 5
                    decrementY -= 0.05f
                }

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
