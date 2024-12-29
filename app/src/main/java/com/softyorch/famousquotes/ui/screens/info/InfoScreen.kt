package com.softyorch.famousquotes.ui.screens.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.core.commonComponents.TopBarStandard
import com.softyorch.famousquotes.ui.theme.AppColorSchema

@Composable
fun InfoScreen(modifier: Modifier = Modifier, leftHanded: Boolean, onUpNavigation: () -> Unit) {

    val paddingTop = with(LocalDensity.current) {
        WindowInsets.statusBars.getTop(this).toDp() + 24.dp
    }

    Scaffold(
        topBar = {
            TopBarStandard(
                modifier = modifier,
                paddingTop = paddingTop,
                leftHanded = leftHanded,
                textTitle = "Información",
                iconTitle = Icons.Default.Info,
                onUpNavigation = onUpNavigation
            )
        }
    ) { dp ->
        Column(
            modifier = modifier
                .padding(dp)
                .fillMaxSize()
                .background(color = AppColorSchema.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card {
                Image(
                    painter = painterResource(R.drawable.under_construction),
                    contentDescription = "Info",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.background(
                        color = AppColorSchema.background,
                        shape = MaterialTheme.shapes.large
                    )
                )
            }
        }
    }
}
