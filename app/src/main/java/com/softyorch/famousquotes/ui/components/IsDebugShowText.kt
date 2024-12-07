package com.softyorch.famousquotes.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.BuildConfig

@Composable
fun IsDebugShowText(id: String) {
    if (BuildConfig.DEBUG) {
        val context = LocalContext.current

        fun copyToClipboard(context: Context, text: String) {
            val clipboardManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Id quote", text)
            clipboardManager.setPrimaryClip(clip)
        }

        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = id,
                modifier = Modifier
                    .background(Color.Yellow.copy(alpha = 0.8f), shape = MaterialTheme.shapes.large)
                    .padding(8.dp)
                    .clickable {
                        copyToClipboard(context, id)
                    }
            )
        }
    }
}