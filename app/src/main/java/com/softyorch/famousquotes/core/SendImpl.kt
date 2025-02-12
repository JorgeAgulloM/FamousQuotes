package com.softyorch.famousquotes.core

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.FileProvider
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.theme.AppColorSchema
import com.softyorch.famousquotes.ui.utils.extFunc.getResourceString
import com.softyorch.famousquotes.utils.showToast
import com.softyorch.famousquotes.utils.writeLog
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class SendImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatcherDefault: CoroutineDispatcher = Dispatchers.Default,
) : ISend {

    private val rawName = BuildConfig.APP_TITLE
    private val name = context.getResourceString(rawName)
    private val startLink = "https://softyorch.com"

    override suspend fun shareTextTo(data: String) {
        val sendText = "$data\n\n$name\n\n${selectLink()}"

        context.startActivity(
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, sendText)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }

    override suspend fun shareImageTo(data: String, imageUri: String) {
        val subtitle = context.getResourceString("main_text_get_inspired")
        downloadImageAndSaveToTempFile(imageUri, name, data, subtitle)?.let { uri ->
            context.startActivity(
                Intent(Intent.ACTION_SEND).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_TEXT, selectLink())
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            )
            appLinkCopyToClipBoard()
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

        return withContext(dispatcherDefault) {
            val result = (imageLoader.execute(request) as? SuccessResult)?.drawable
            if (result !is BitmapDrawable) return@withContext null

            // Crea una copia mutable del Bitmap
            val mutableBitmap = result.bitmap.copy(Bitmap.Config.ARGB_8888, true)
            var x = mutableBitmap.width / 2f // Posición horizontal (centrado)
            var y = mutableBitmap.height / 1.28f // Posición vertical (centrado)

            // Crea un Canvas para dibujar en el Bitmap mutable
            val canvas = Canvas(mutableBitmap)

            // Configuración del icono
            val iconBitmap =
                BitmapFactory.decodeResource(context.resources, R.drawable.icon_quote)
            val paintIcon = Paint().apply {
                colorFilter =
                    PorterDuffColorFilter(AppColorSchema.secondary.toArgb(), PorterDuff.Mode.SRC_IN)
                setShadowLayer(20f, 2f, 2f, AppColorSchema.text.toArgb())
            }

            val scaledIconBitmap = Bitmap.createScaledBitmap(iconBitmap, 70, 70, true)

            // Configuración de los textos del título
            val paintTitleTop = Paint().apply {
                color = AppColorSchema.text.toArgb()
                textSize = 40f
                textAlign = Paint.Align.LEFT // Alineación a la izquierda
                typeface = Typeface.DEFAULT_BOLD
                setShadowLayer(20f, 2f, 2f, AppColorSchema.shadowText.toArgb())
            }

            val paintTitleBottom = Paint().apply { // Mismo estilo que titleTop
                color = AppColorSchema.text.toArgb()
                textSize = 40f
                textAlign = Paint.Align.LEFT
                typeface = Typeface.DEFAULT_BOLD
                setShadowLayer(20f, 2f, 2f, AppColorSchema.shadowText.toArgb())
            }

            val titleTop = titleToDraw.split(" ")[0]
            val titleBottom = titleToDraw.split(" ")[1]

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
            val iconWidth = scaledIconBitmap.width

            // El icono se centra verticalmente entre las dos palabras del título
            val xTitle = xIcon + iconWidth + 10 // Posición de las palabras a la derecha del icono
            val yTitleTop = yIcon + boundsTitleTop.height()
            val yTitleBottom = yTitleTop + boundsTitleBottom.height()

            val gradientColors = intArrayOf(
                Color.Transparent.toArgb(),
                AppColorSchema.background.toArgb(),
                AppColorSchema.background.toArgb(),
                AppColorSchema.background.toArgb(),
                AppColorSchema.background.toArgb(),
                AppColorSchema.background.toArgb()
            )

            // De negro semitransparente a transparente
            val gradientPositions = floatArrayOf(
                0f,
                0.2f,
                0.4f,
                0.6f,
                0.8f,
                1f
            )

            // Posiciones de los colores en el degradado
            val gradient = LinearGradient(
                0f,
                yTitleTop - 90f, // Coordenada x de inicio (izquierda)
                0f,
                y + mutableBitmap.height / 1.22f, // Coordenada y de fin (abajo)
                gradientColors,
                gradientPositions,
                Shader.TileMode.CLAMP
            )

            // Configuración del Paint para el fondo
            val paintBackground = Paint()
            paintBackground.shader = gradient

            // Calcular el área que ocupan los textos (incluyendo el icono)
            val left = 0f // Considerar la posición del texto más a la izquierda
            val top = yTitleTop - 40f
            val right = mutableBitmap.width.toFloat()
            val bottom = y + mutableBitmap.height / 1.22f

            // Dibujar el fondo degradado
            val rectBackground = RectF(left, top, right, bottom)
            canvas.drawRect(rectBackground, paintBackground)

            // Dibujar el icono y las palabras del título
            canvas.drawBitmap(scaledIconBitmap, xIcon, yIcon, paintIcon)
            canvas.drawText(titleTop, xTitle, yTitleTop, paintTitleTop)
            canvas.drawText(titleBottom, xTitle, yTitleBottom, paintTitleBottom)

            canvas.save()

            // Configura el título para el texto
            val subtitlePaint = Paint().apply {
                color = AppColorSchema.secondary.toArgb() // Color del texto
                textSize = 60f // Tamaño del texto
                textAlign = Paint.Align.CENTER // Alineación del texto
                typeface = Typeface.DEFAULT_BOLD
                setShadowLayer(10f, 2f, 2f, AppColorSchema.shadowText.toArgb())
            }

            // Dibuja el texto en el Canvas
            canvas.drawText(subtitle, x, y, subtitlePaint)

            val quotePaint = Paint().apply {
                color = AppColorSchema.text.toArgb() // Color del texto
                textSize = 60f // Tamaño del texto
                textAlign = Paint.Align.CENTER // Alineación del texto
                typeface = Typeface.DEFAULT_BOLD
                setShadowLayer(10f, 1f, 1f, AppColorSchema.shadowText.toArgb())
            }

            val margin = 16 * 1.5f // Convertir dp a píxeles
            val maxWidth = mutableBitmap.width.toFloat() - (margin * 2) // Ancho máximo disponible con márgenes
            val words = textToDraw.split(" ") // Dividir el texto en palabras
            var currentLine = ""
            var decrementY = 0.0f

            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"

                // Comprobar si la línea actual con la nueva palabra cabe en el ancho permitido
                if (quotePaint.measureText(testLine) <= maxWidth) {
                    currentLine = testLine // Añadir la palabra a la línea actual
                } else {
                    // Dibujar la línea actual en el canvas
                    x = margin + (maxWidth / 2) // Centrar el texto teniendo en cuenta los márgenes
                    y = mutableBitmap.height / (1.20f + decrementY)
                    canvas.drawText(currentLine, x, y, quotePaint)

                    // Comenzar una nueva línea con la palabra que no cabía
                    currentLine = word
                    decrementY -= 0.05f // Ajustar el espacio entre líneas
                }
            }

            // Dibujar la última línea si queda texto
            if (currentLine.isNotEmpty()) {
                x = margin + (maxWidth / 2)
                y = mutableBitmap.height / (1.20f + decrementY)
                canvas.drawText(currentLine, x, y, quotePaint)
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
                BuildConfig.PROVIDER_AUTHORITIES,
                file
            )
        }
    }

    private suspend fun appLinkCopyToClipBoard() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("App Link", selectLink())
        clipboard.setPrimaryClip(clip)
        val message = context.getResourceString("share_toast_link")
        withContext(Dispatchers.Main) { context.showToast(message, Toast.LENGTH_LONG) }
    }

    private fun selectLink(): String {
        return when (rawName) {
            "app_name_historical" -> "$startLink/historicalquotes"
            "app_name_uplifting" -> "$startLink/positivequotes"
            "app_name_biblical" -> "$startLink/biblicalquotes"
            else -> ""
        }.also {
            writeLog(text = "Link for name: $rawName -> $it")
        }
    }
}

interface ISend {
    suspend fun shareTextTo(data: String)
    suspend fun shareImageTo(data: String, imageUri: String)
}
