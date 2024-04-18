package com.softyorch.famousquotes.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.softyorch.famousquotes.ui.admob.Banner
import com.softyorch.famousquotes.ui.theme.MyTypography
import com.softyorch.famousquotes.ui.theme.PrimaryColor
import com.softyorch.famousquotes.ui.theme.brushBackGround

@Composable
fun HomeScreen(viewModel: HomeViewModel) {

    val state: HomeState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(contentAlignment = Alignment.Center) {
        BackgroundImage(uri = state.quote.imageUrl)
        CardQuote(body = state.quote.body, owner = state.quote.owner)
    }
}

@Composable
fun BackgroundImage(uri: String) {
    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = uri,
            modifier = Modifier.fillMaxWidth().height(300.dp),
            contentDescription = "image",
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
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(brush = brushBackGround())
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Controls()
                    TextHome(text = "\"$body\"", true)
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
fun Controls() {
    Row(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(end = 16.dp),
        horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.Top
    ) {
        IconButtonMenu(text = "Info", icon = Icons.Outlined.Info) {}
        IconButtonMenu(text = "Otra frase", icon = Icons.Outlined.Refresh) {}
        IconButtonMenu(text = "Compartir", icon = Icons.Outlined.Send) {}
    }
}

@Composable
fun IconButtonMenu(text: String, icon: ImageVector, onClick: () -> Unit) {
    IconButton(
        onClick = { onClick() }, colors = IconButtonDefaults.iconButtonColors(
            contentColor = PrimaryColor
        )
    ) {
        Icon(imageVector = icon, contentDescription = text)
    }
}

@Composable
fun TextHome(text: String, isBody: Boolean = false) {
    val style = if (isBody) MyTypography.displayLarge else MyTypography.labelLarge
    Text(
        text = text,
        modifier = Modifier.padding(horizontal = 16.dp),
        style = style
    )
}
