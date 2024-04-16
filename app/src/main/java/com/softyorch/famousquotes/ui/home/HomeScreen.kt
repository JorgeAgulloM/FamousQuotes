package com.softyorch.famousquotes.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.admob.Banner
import com.softyorch.famousquotes.ui.theme.PanelColor

@Composable
fun HomeScreen(viewModel: HomeViewModel) {

    val state: HomeState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(contentAlignment = Alignment.Center) {
        BackgroundImage()
        CardQuote(body = state.quote.body, owner = state.quote.owner)
    }
}

@Composable
fun BackgroundImage() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_image_9),
            modifier = Modifier.fillMaxWidth().height(300.dp),
            contentDescription = "Background",
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun CardQuote(body: String, owner: String) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f))
        ElevatedCard(
            modifier = Modifier.fillMaxWidth().weight(2f),
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Column(
                modifier = Modifier
                    .border(
                        BorderStroke(1.dp, Color.DarkGray),
                        shape = MaterialTheme.shapes.extraLarge
                    )
                    .background(color = PanelColor)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(24.dp))
                    TextHome(
                        text = "\"$body\"",
                        true
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        TextHome(text = "\"$owner\"")
                    }
                }
                Banner()
            }
        }
    }
}

@Composable
fun TextHome(text: String, isBody: Boolean = false) {
    val style = if (isBody) MaterialTheme.typography.headlineSmall.copy(
        textAlign = TextAlign.Center,
        fontFamily = FontFamily.Cursive,
        fontWeight = FontWeight.Bold
    ) else MaterialTheme.typography.bodyLarge
    Text(text = text, modifier = Modifier.padding(16.dp).fillMaxWidth(), style = style)
}
