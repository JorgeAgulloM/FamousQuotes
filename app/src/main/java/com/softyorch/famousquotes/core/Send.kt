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

    suspend fun shareTextTo(data: String) {
        val htmlFormattedMessage =
            "https://play.google.com/store/apps/details?id=com.softyorch.famousquotes.$pkgName"
        val sendText = "$data\n\n$name\n\n$htmlFormattedMessage"

        context.startActivity(
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, sendText)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }

    suspend fun shareImageTo(data: String, imageUri: String) {
        val subtitle = context.getResourceString("main_text_get_inspired")
        downloadImageAndSaveToTempFile(imageUri, name, data, subtitle)?.let { uri ->
            context.startActivity(
                Intent(Intent.ACTION_SEND).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            )
        }
    }

    private suspend fun downloadImageAndSaveToTempFile(
        imageUrl: String,
        titleToDraw: String,
        textToDraw: String,
        subtitle: String,
    ): Uri? {
        val imageLoader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .build()

        return withContext(Dispatchers.IO) {
            val result = (imageLoader.execute(request) as? SuccessResult)?.drawable
            if (result is BitmapDrawable) {

                val titleTop = titleToDraw.split(" ")[0]
                val titleBottom = titleToDraw.split(" ")[1]

                // Crea una copia mutable del Bitmap
                val mutableBitmap = result.bitmap.copy(Bitmap.Config.ARGB_8888, true)

                // Crea un Canvas para dibujar en el Bitmap mutable
                val canvas = Canvas(mutableBitmap)

                // Configuración del icono
                val iconBitmap =
                    BitmapFactory.decodeResource(context.resources, R.drawable.icon_quote)
                val paintIcon = Paint().apply {
                    colorFilter =
                        PorterDuffColorFilter(SecondaryColor.toArgb(), PorterDuff.Mode.SRC_IN)
                    setShadowLayer(20f, 2f, 2f, Color.Black.toArgb())
                }

                val scaledIconBitmap = Bitmap.createScaledBitmap(iconBitmap, 70, 70, true)
                val iconWidth = scaledIconBitmap.width

                // Configuración de los textos del título
                val paintTitleTop = Paint().apply {
                    color = WhiteSmoke.toArgb()
                    textSize = 40f
                    textAlign = Paint.Align.LEFT // Alineación a la izquierda
                    typeface = Typeface.DEFAULT_BOLD
                    setShadowLayer(20f, 2f, 2f, Color.Black.toArgb())
                }
                val paintTitleBottom = Paint().apply { // Mismo estilo que titleTop
                    color = WhiteSmoke.toArgb()
                    textSize = 40f
                    textAlign = Paint.Align.LEFT
                    typeface = Typeface.DEFAULT_BOLD
                    setShadowLayer(20f, 2f, 2f, Color.Black.toArgb())
                }


                // Cálculo de posiciones para el título
                val boundsTitleTop = Rect()
                paintTitleTop.getTextBounds(titleTop, 0, titleTop.length, boundsTitleTop)
                val boundsTitleBottom = Rect()
                paintTitleBottom.getTextBounds(
                    titleBottom,
                    0,
                    titleBottom.length,
                    boundsTitleBottom
                )

                val xIcon = mutableBitmap.width / 2.7f // Posición del icono a la izquierda
                val yIcon =
                    mutableBitmap.height / 1.40f - (boundsTitleTop.height() + boundsTitleBottom.height()) / 2
                // El icono se centra verticalmente entre las dos palabras del título

                val xTitle =
                    xIcon + iconWidth + 10 // Posición de las palabras a la derecha del icono
                val yTitleTop = yIcon + boundsTitleTop.height()
                val yTitleBottom = yTitleTop + boundsTitleBottom.height()

                // Dibujar el icono y las palabras del título
                canvas.drawBitmap(scaledIconBitmap, xIcon, yIcon, paintIcon)
                canvas.drawText(titleTop, xTitle, yTitleTop, paintTitleTop)
                canvas.drawText(titleBottom, xTitle, yTitleBottom, paintTitleBottom)

                canvas.save()


                // Configura el título para el texto
                val subtitlePaint = Paint().apply {
                    color = SecondaryColor.toArgb() // Color del texto
                    textSize = 60f // Tamaño del texto
                    textAlign = Paint.Align.CENTER // Alineación del texto
                    typeface = Typeface.DEFAULT_BOLD
                    setShadowLayer(10f, 2f, 2f, Color.Black.toArgb())
                }

                // Dibuja el texto en el Canvas
                var x = mutableBitmap.width / 2f // Posición horizontal (centrado)
                var y = mutableBitmap.height / 1.28f // Posición vertical (centrado)
                canvas.drawText(subtitle, x, y, subtitlePaint)


                val quotePaint = Paint().apply {
                    color = Color.White.toArgb() // Color del texto
                    textSize = 60f // Tamaño del texto
                    textAlign = Paint.Align.CENTER // Alineación del texto
                    typeface = Typeface.DEFAULT_BOLD
                    setShadowLayer(10f, 1f, 1f, Color.Black.toArgb())
                }

                val palabras = textToDraw.split(" ") // Dividir el texto en palabras

                var indice = 0
                var decrementY = 0.0f
                while (indice < palabras.size) {
                    val grupo = palabras.subList(indice, minOf(indice + 6, palabras.size))
                    x = mutableBitmap.width / 2f
                    y = mutableBitmap.height / (1.20f + decrementY)
                    canvas.drawText(grupo.joinToString(" "), x, y, quotePaint)
                    indice += 6
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
