package com.softyorch.famousquotes.ui.screens.detail.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.core.commonComponents.IsDebugShowText
import com.softyorch.famousquotes.ui.core.commonComponents.TextOwner
import com.softyorch.famousquotes.ui.screens.detail.DetailActions
import com.softyorch.famousquotes.ui.screens.detail.model.DetailState
import com.softyorch.famousquotes.ui.screens.detail.model.QuoteDetailsModel
import com.softyorch.famousquotes.ui.screens.detail.model.QuoteDetailsModel.Companion.toFamousQuoteModel
import com.softyorch.famousquotes.ui.theme.BackgroundColor
import com.softyorch.famousquotes.ui.theme.TextStandardColor
import com.softyorch.famousquotes.ui.theme.WhiteSmoke
import com.softyorch.famousquotes.utils.showToast

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