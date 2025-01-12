package com.softyorch.famousquotes.ui.core.commonComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.domain.model.FamousQuoteModel
import com.softyorch.famousquotes.ui.theme.AppColorSchema
import com.softyorch.famousquotes.ui.utils.extFunc.copyToClipboard

@Composable
fun IsDebugShowText(quote: FamousQuoteModel) {
    if (BuildConfig.DEBUG && false == true) {
        val context = LocalContext.current

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = quote.id,
                modifier = Modifier
                    .background(AppColorSchema.favoriteColor.copy(alpha = 0.8f), shape = MaterialTheme.shapes.large)
                    .padding(8.dp)
                    .clickable { context.copyToClipboard("Id quote", quote.id) }
            )
        }
    }
}
