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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.Share
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
import androidx.compose.ui.graphics.Color
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
import com.softyorch.famousquotes.ui.core.commonComponents.IconButtonMenu
import com.softyorch.famousquotes.ui.core.commonComponents.IconCard
import com.softyorch.famousquotes.ui.core.commonComponents.IsDebugShowText
import com.softyorch.famousquotes.ui.core.commonComponents.SpacerIconButton
import com.softyorch.famousquotes.ui.screens.detail.model.QuoteDetailsModel
import com.softyorch.famousquotes.ui.screens.detail.model.QuoteDetailsModel.Companion.toFamousQuoteModel
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

    val quote: QuoteDetailsModel by viewModel.quoteModel.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.setDetailAction(DetailActions.LoadQuoteData(), id)
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
                animatedVisibilityScope = animatedVisibilityScope
            ) { action -> viewModel.setDetailAction(action, id) }
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
    quote: QuoteDetailsModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onAction: (DetailActions) -> Unit
) {
    val context = LocalContext.current
    with(sharedTransitionScope) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            if (quote.imageUrl.isEmpty()) {
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

                        CardControls(quote = quote, onAction = onAction)
                    }
                }
                IsDebugShowText(quote.toFamousQuoteModel())
            }
        }
    }
}

@Composable
fun CardControls(
    modifier: Modifier = Modifier,
    quote: QuoteDetailsModel,
    onAction: (DetailActions) -> Unit
) {
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
        quote.let {
            IconCard(
                cDescription = "Like",
                icon = Icons.Default.FavoriteBorder,
                secondIcon = Icons.Default.Favorite,
                color = Color.Red,
                valueStatistic = quote.likes,
                isSelected = quote.isLiked,
            ) { onAction(DetailActions.SetLikeQuote()) }
            IconCard(
                cDescription = "Favorite",
                icon = Icons.Default.StarOutline,
                secondIcon = Icons.Default.Star,
                color = Color.Yellow,
                valueStatistic = quote.favorites,
                isSelected = quote.isFavorite
            ) { onAction(DetailActions.SetFavoriteQuote()) }
            IconCard(
                cDescription = "Share",
                icon = Icons.Outlined.Share,
                colorIcon = PrimaryColor
            ) { onAction(DetailActions.ShareQuoteAsText()) }
            IconCard(
                cDescription = "Download",
                icon = Icons.Outlined.Download,
                colorIcon = PrimaryColor
            ) { onAction(DetailActions.DownloadQuote()) }
            IconCard(
                cDescription = "Shown",
                icon = Icons.Outlined.RemoveRedEye,
                colorIcon = PrimaryColor,
                valueStatistic = quote.showns
            ) { }
        }
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