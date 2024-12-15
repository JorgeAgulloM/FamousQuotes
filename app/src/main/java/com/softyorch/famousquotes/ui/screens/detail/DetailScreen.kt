package com.softyorch.famousquotes.ui.screens.detail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.domain.model.FamousQuoteModel
import com.softyorch.famousquotes.ui.components.IsDebugShowText
import com.softyorch.famousquotes.ui.core.commonComponents.IconButtonMenu
import com.softyorch.famousquotes.ui.core.commonComponents.SpacerIconButton
import com.softyorch.famousquotes.ui.screens.detail.model.PropertyStatisticsModel
import com.softyorch.famousquotes.ui.theme.BackgroundColor
import com.softyorch.famousquotes.ui.theme.PrimaryColor
import com.softyorch.famousquotes.ui.theme.SecondaryColor
import com.softyorch.famousquotes.ui.theme.WhiteSmoke

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    id: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: DetailViewModel,
    onBackNavigation: () -> Unit
) {

    val quote: FamousQuoteModel by viewModel.quote.collectAsStateWithLifecycle()
    val propertyList: List<PropertyStatisticsModel> by viewModel.propertyList.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.apply {
            getQuote(id)
        }
    }

    Scaffold(modifier = modifier.fillMaxSize(), containerColor = BackgroundColor) { paddingValues ->
        Box(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            CardDetail(
                quote = quote,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                properties = propertyList
            )
            Row(
                modifier = modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButtonMenu(
                    cDescription = "Back",
                    color = SecondaryColor.copy(alpha = 0.6f),
                    icon = Icons.AutoMirrored.Outlined.ArrowBack,
                    shadowOn = true
                ) { onBackNavigation() }
                SpacerIconButton()
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CardDetail(
    modifier: Modifier = Modifier,
    quote: FamousQuoteModel?,
    properties: List<PropertyStatisticsModel>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val context = LocalContext.current
    with(sharedTransitionScope) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            if (quote == null || quote.imageUrl.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Card(
                    modifier = Modifier
                        .sharedElement(
                            state = rememberSharedContentState(key = "image-${quote.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.large),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = WhiteSmoke
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                ) {

                    val painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context)
                            .data(quote.imageUrl)
                            .crossfade(true) // Set the target size to load the image at.
                            .build(),
                        contentScale = ContentScale.Fit
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(top = 4.dp, start = 4.dp, end = 4.dp)
                                .fillMaxWidth()
                                .height(sixtyPercentOfScreen()),
                            shape = MaterialTheme.shapes.large,
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                        ) {
                            Image(
                                painter = painter,
                                contentDescription = stringResource(R.string.main_content_desc_image),
                                modifier = Modifier
                                    .fillMaxWidth(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Text(
                            text = quote.body,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                textAlign = TextAlign.Center,
                                fontStyle = FontStyle.Italic,
                                shadow = Shadow(
                                    color = BackgroundColor.copy(alpha = 0.6f),
                                    blurRadius = 4f
                                )
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, start = 16.dp, end = 8.dp)
                        )

                        CardStatisticQuote(properties)

                        CardControls()
                    }
                }
                IsDebugShowText(quote)
            }
        }
    }
}

@Composable
fun CardStatisticQuote(propertyList: List<PropertyStatisticsModel>) {
    Column {
        propertyList.forEach { properties ->
            StatisticProperty(properties)
        }
    }
}

@Composable
fun StatisticProperty(
    properties: PropertyStatisticsModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButtonMenu(
            cDescription = properties.name,
            icon = properties.icon,
            colorIcon = PrimaryColor
        ) { }
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = properties.value.toString())
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = properties.name)
    }
}

@Composable
fun CardControls(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(start = 4.dp, end = 4.dp, bottom = 4.dp)
            .fillMaxWidth()
            .background(
                color = BackgroundColor.copy(alpha = 0.6f),
                shape = MaterialTheme.shapes.large
            ),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButtonMenu(
            cDescription = "Like",
            icon = Icons.Outlined.FavoriteBorder,
            colorIcon = PrimaryColor
        ) { }
        IconButtonMenu(
            cDescription = "Favorite",
            icon = Icons.Outlined.Star,
            colorIcon = PrimaryColor
        ) { }
        IconButtonMenu(
            cDescription = "Share",
            icon = Icons.Outlined.Share,
            colorIcon = PrimaryColor
        ) { }
        IconButtonMenu(
            cDescription = "Download",
            icon = Icons.Outlined.Download,
            colorIcon = PrimaryColor
        ) { }
    }
}


@Composable
fun sixtyPercentOfScreen(): Dp {
    val configuration = LocalConfiguration.current
    val screenHeightPx =
        configuration.screenHeightDp.dp
    val heightInDp = (screenHeightPx.value * 0.55f).dp

    return heightInDp
}