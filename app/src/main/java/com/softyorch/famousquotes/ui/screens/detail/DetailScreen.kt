package com.softyorch.famousquotes.ui.screens.detail

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.admob.Banner
import com.softyorch.famousquotes.ui.core.commonComponents.IconButtonMenu
import com.softyorch.famousquotes.ui.core.commonComponents.IconCard
import com.softyorch.famousquotes.ui.core.commonComponents.IsDebugShowText
import com.softyorch.famousquotes.ui.core.commonComponents.LoadingCircle
import com.softyorch.famousquotes.ui.core.commonComponents.SpacerIconButton
import com.softyorch.famousquotes.ui.screens.detail.model.DetailState
import com.softyorch.famousquotes.ui.screens.detail.model.QuoteDetailsModel
import com.softyorch.famousquotes.ui.screens.detail.model.QuoteDetailsModel.Companion.toFamousQuoteModel
import com.softyorch.famousquotes.ui.screens.home.components.BasicDialogApp
import com.softyorch.famousquotes.ui.screens.home.components.NoConnectionDialog
import com.softyorch.famousquotes.ui.screens.home.components.TextOwner
import com.softyorch.famousquotes.ui.theme.BackgroundColor
import com.softyorch.famousquotes.ui.theme.FavoriteColor
import com.softyorch.famousquotes.ui.theme.LikeColor
import com.softyorch.famousquotes.ui.theme.PrimaryColor
import com.softyorch.famousquotes.ui.theme.SecondaryColor
import com.softyorch.famousquotes.ui.theme.TextStandardColor
import com.softyorch.famousquotes.ui.theme.WhiteSmoke
import com.softyorch.famousquotes.ui.utils.DialogCloseAction.DISMISS
import com.softyorch.famousquotes.ui.utils.DialogCloseAction.NEGATIVE
import com.softyorch.famousquotes.ui.utils.DialogCloseAction.POSITIVE
import com.softyorch.famousquotes.utils.showToast

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
    val state: DetailState by viewModel.detailState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.setDetailAction(DetailActions.LoadQuoteData(), id)
    }

    val finishAnimation =
        animatedVisibilityScope.transition.currentState == EnterExitState.Visible && !state.hideControls

    Scaffold(modifier = modifier.fillMaxSize(), containerColor = BackgroundColor) { paddingValues ->
        Column(
            modifier = modifier.padding(paddingValues),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                CardDetail(
                    id = id,
                    quote = quote,
                    state = state,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    finishAnimation = finishAnimation
                ) { action -> viewModel.setDetailAction(action, id) }
                if (finishAnimation) Row(
                    modifier = modifier
                        .padding(start = 16.dp, top = 8.dp)
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
            Box(Modifier.height(Banner.heightBanner.dp)) {}
        }
        SetDialogs(state) {
            viewModel.setDetailAction(it, id)
        }
    }
}

@Composable
fun SetDialogs(state: DetailState, onActions: (DetailActions) -> Unit) {
    if (state.showDialogNoConnection) NoConnectionDialog {
        onActions(DetailActions.ShowNoConnectionDialog())
    }

    if (state.isLoading) LoadingCircle()

    if (state.shareAs) BasicDialogApp(
        text = stringResource(R.string.dialog_how_do_you_share),
        title = stringResource(R.string.dialog_share_title),
        textBtnPositive = stringResource(R.string.dialog_share_by_text),
        textBtnNegative = stringResource(R.string.dialog_share_by_image),
        blackDismissActions = true
    ) {
        when (it) {
            POSITIVE -> onActions(DetailActions.ShareQuoteAs(shareAs = ShareAs.Text))
            NEGATIVE -> onActions(DetailActions.ShareQuoteAs(shareAs = ShareAs.Image))
            DISMISS -> onActions(DetailActions.HowToShareQuote())
        }
    }

}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CardDetail(
    id: String,
    modifier: Modifier = Modifier,
    quote: QuoteDetailsModel,
    state: DetailState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    finishAnimation: Boolean,
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
                val toastMsg = stringResource(R.string.main_info_dialog_connection)

                Card(
                    modifier = Modifier
                        .sharedElement(
                            state = rememberSharedContentState(key = "image-$id"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        .padding(horizontal = 8.dp)
                        .fillMaxSize()
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
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {

                        val cardShape = MaterialTheme.shapes.large

                        Card(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                                .clip(cardShape)
                                .clickable { onAction(DetailActions.HideControls()) }
                                .weight(1f),
                            shape = cardShape,
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                        ) {
                            Image(
                                painter = painter,
                                contentDescription = stringResource(R.string.main_content_desc_image),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        AnimatedVisibility(finishAnimation) {
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
                        }

                        AnimatedVisibility(finishAnimation) {
                            TextOwner(
                                text = quote.owner,
                                color = TextStandardColor,
                                isHiPadding = false
                            ) {
                                if (state.hasConnection) onAction(DetailActions.OwnerQuoteIntent())
                                else context.showToast(toastMsg, Toast.LENGTH_LONG)
                            }
                        }
                        AnimatedVisibility(finishAnimation) {
                            CardControls(quote = quote, state = state, onAction = onAction)
                        }
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
    state: DetailState,
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
                color = LikeColor,
                valueStatistic = quote.likes,
                isEnabled = state.hasConnection,
                isSelected = quote.isLiked,
            ) { onAction(DetailActions.SetLikeQuote()) }
            IconCard(
                cDescription = "Favorite",
                icon = Icons.Default.StarOutline,
                secondIcon = Icons.Default.Star,
                color = FavoriteColor,
                valueStatistic = quote.favorites,
                isEnabled = state.hasConnection,
                isSelected = quote.isFavorite
            ) { onAction(DetailActions.SetFavoriteQuote()) }
            IconCard(
                cDescription = "Share",
                icon = Icons.Outlined.Share,
                colorIcon = PrimaryColor,
                isEnabled = state.hasConnection,
            ) { onAction(DetailActions.HowToShareQuote()) }
            IconCard(
                cDescription = "Download",
                icon = Icons.Outlined.Download,
                colorIcon = PrimaryColor,
                isEnabled = state.hasConnection,
            ) { onAction(DetailActions.DownloadQuote()) }
            IconCard(
                cDescription = "Shown",
                icon = Icons.Outlined.RemoveRedEye,
                colorIcon = PrimaryColor,
                isEnabled = state.hasConnection,
                valueStatistic = quote.showns
            ) { }
        }
    }
}
