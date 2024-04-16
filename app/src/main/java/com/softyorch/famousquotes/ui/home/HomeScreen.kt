package com.softyorch.famousquotes.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.admob.Banner
import com.softyorch.famousquotes.ui.theme.PanelColor

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    Box(contentAlignment = Alignment.Center) {
        BackgroundImage()
        CardQuote()
    }
}

@Composable
fun BackgroundImage() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_image_5),
            modifier = Modifier.fillMaxWidth().height(300.dp),
            contentDescription = "Background",
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun CardQuote() {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f))
        ElevatedCard(
            modifier = Modifier.fillMaxWidth().weight(2f),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier.background(color = PanelColor).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(24.dp))
                    TextHome(text = "Debes seguir tu camino!!", true)
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        TextHome(text = "\"Yorch, the creator\"")
                    }
                }
                Banner()
            }
        }
    }
}

@Composable
fun TextHome(text: String, isBody: Boolean = false) {
    val style =
        if (isBody) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.bodyLarge
    Text(text = text, modifier = Modifier.padding(16.dp), style = style)
}
