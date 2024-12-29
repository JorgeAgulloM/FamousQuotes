package com.softyorch.famousquotes.ui.screens.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.admob.Banner
import com.softyorch.famousquotes.ui.core.commonComponents.AppVersionText
import com.softyorch.famousquotes.ui.core.commonComponents.TopBarStandard
import com.softyorch.famousquotes.ui.screens.home.components.AppIcon
import com.softyorch.famousquotes.ui.screens.home.components.HeaderSubtitleApp
import com.softyorch.famousquotes.ui.screens.home.components.SpacerHeight
import com.softyorch.famousquotes.ui.theme.AppColorSchema
import com.softyorch.famousquotes.ui.utils.extFunc.getResourceString

@Composable
fun InfoScreen(
    modifier: Modifier = Modifier,
    leftHanded: Boolean,
    darkTheme: Boolean,
    onUpNavigation: () -> Unit
) {

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
        val scrollState = rememberScrollState()

        Column(
            modifier = modifier
                .padding(dp)
                .fillMaxWidth()
                .verticalScroll(state = scrollState)
                .background(color = AppColorSchema.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CardSoftYorch(modifier = modifier)
            TextInfo(leftHanded = leftHanded)
            IconsInfo(leftHanded = leftHanded, darkTheme = darkTheme)
            TitleAppInfo()
            SpacerHeight(Banner.heightBanner + 32)
        }
    }
}

@Composable
private fun CardSoftYorch(modifier: Modifier) {
    SpacerHeight()
    Card(
        modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.softyorch),
            contentDescription = "Logo",
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun TextInfo(modifier: Modifier = Modifier, leftHanded: Boolean) {

    val rawName = BuildConfig.APP_TITLE
    val nameApp = LocalContext.current.getResourceString(rawName)

    SpacerHeight(32)
    IconButtonInfo(
        leftHanded = leftHanded,
        iconButton = Icons.Default.Info,
        iconDescription = "Info",
        text = nameApp
    )
    AppVersionText(isCenter = false) {}
    HorizontalDivider(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        thickness = 1.dp,
        color = AppColorSchema.secondary
    )
    SpacerHeight()
    Text(
        text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur vel quam eget tortor faucibus feugiat. Aliquam erat volutpat. Aenean vehicula, ex id pharetra vulputate, purus neque varius libero, a facilisis risus sapien vel sapien. Integer fringilla dolor sit amet nibh sodales, ac facilisis arcu condimentum. Nullam porttitor, nulla at pellentesque posuere, metus erat pellentesque felis, nec laoreet orci erat id nulla. Maecenas interdum odio nec lectus fermentum convallis. Ut placerat, ligula non pellentesque efficitur, arcu lorem gravida sapien, vitae tristique augue odio eget magna. Vestibulum sit amet erat non arcu laoreet dictum.",
        style = MaterialTheme.typography.bodyLarge.copy(color = AppColorSchema.text),
        modifier = Modifier.padding(horizontal = 16.dp)
    )
    SpacerHeight(32)
}

@Composable
private fun IconsInfo(leftHanded: Boolean, darkTheme: Boolean) {
    val buyIcon = if (darkTheme) R.drawable.coffee_white else R.drawable.coffee_black
    val payIcon = if (darkTheme) R.drawable.logo_paypal_white else R.drawable.logo_paypal_black

    IconButtonInfo(
        leftHanded = leftHanded,
        iconButton = Icons.Default.Mail,
        iconDescription = "Support",
        text = "Soporte"
    )
    IconButtonInfo(
        leftHanded = leftHanded,
        imageIconButton = painterResource(buyIcon),
        iconDescription = "Buy Me A Coffee",
        text = "Buy Me A Coffee"
    )
    IconButtonInfo(
        leftHanded = leftHanded,
        imageIconButton = painterResource(payIcon),
        iconDescription = "Coffee with PayPal",
        text = "Coffee with PayPal"
    )
    SpacerHeight(32)
}

@Composable
private fun TitleAppInfo() {
    AppIcon()
    HeaderSubtitleApp()
    AppVersionText {}
}

@Composable
fun IconButtonInfo(
    leftHanded: Boolean,
    iconButton: ImageVector? = null,
    imageIconButton: Painter? = null,
    iconDescription: String,
    text: String,
    onClick: () -> Unit = {}
) {
    val shape = MaterialTheme.shapes.medium

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(shape)
            .clickable { onClick() },
        horizontalArrangement = if (leftHanded) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leftHanded) IconButtonInfoSelected(imageIconButton, iconDescription, iconButton)
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(color = AppColorSchema.text),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        if (!leftHanded) IconButtonInfoSelected(imageIconButton, iconDescription, iconButton)
    }
}

@Composable
private fun IconButtonInfoSelected(
    imageIconButton: Painter?,
    iconDescription: String,
    iconButton: ImageVector?
) {
    val modifier = Modifier.size(36.dp)

    if (imageIconButton != null)
        Image(
            painter = imageIconButton,
            contentDescription = iconDescription,
            modifier = modifier
        )
    if (iconButton != null)
        Icon(
            imageVector = iconButton,
            contentDescription = iconDescription,
            modifier = modifier,
            tint = AppColorSchema.secondary
        )
    if (iconButton == null && imageIconButton == null) Icon(
        imageVector = Icons.Default.LinkOff,
        contentDescription = iconDescription,
        modifier = modifier,
        tint = AppColorSchema.secondary
    )
}
