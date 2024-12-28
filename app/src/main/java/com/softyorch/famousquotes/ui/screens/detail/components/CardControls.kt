package com.softyorch.famousquotes.ui.screens.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.domain.model.QuoteStatistics
import com.softyorch.famousquotes.ui.core.commonComponents.IconCard
import com.softyorch.famousquotes.ui.screens.detail.DetailActions
import com.softyorch.famousquotes.ui.screens.detail.model.DetailState
import com.softyorch.famousquotes.ui.screens.detail.model.QuoteDetailsModel
import com.softyorch.famousquotes.ui.theme.AppColorSchema

@Composable
fun CardControls(
    modifier: Modifier = Modifier,
    quote: QuoteDetailsModel,
    state: DetailState,
    statistics: QuoteStatistics,
    onAction: (DetailActions) -> Unit
) {
    Row(
        modifier = modifier
            .padding(start = 4.dp, end = 4.dp, bottom = 4.dp)
            .fillMaxWidth()
            .background(
                color = AppColorSchema.background.copy(alpha = 0.6f),
                shape = MaterialTheme.shapes.large
            ),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        quote.let {
            IconCard(
                cDescription = "Like",
                icon = Icons.Default.FavoriteBorder,
                secondIcon = Icons.Default.Favorite,
                color = AppColorSchema.likeColor,
                colorIcon = AppColorSchema.whiteSmoke,
                valueStatistic = statistics.likes,
                isEnabled = state.hasConnection,
                isSelected = quote.isLiked,
            ) { onAction(DetailActions.SetLikeQuote()) }
            IconCard(
                cDescription = "Favorite",
                icon = Icons.Default.StarOutline,
                secondIcon = Icons.Default.Star,
                color = AppColorSchema.favoriteColor,
                colorIcon = AppColorSchema.whiteSmoke,
                valueStatistic = statistics.favorites,
                isEnabled = state.hasConnection,
                isSelected = quote.isFavorite
            ) { onAction(DetailActions.SetFavoriteQuote()) }
            IconCard(
                cDescription = "Share",
                icon = Icons.Outlined.Share,
                colorIcon = AppColorSchema.whiteSmoke,
                isEnabled = state.hasConnection,
            ) { onAction(DetailActions.HowToShareQuote()) }
            IconCard(
                cDescription = "Download",
                icon = Icons.Outlined.Download,
                colorIcon = AppColorSchema.whiteSmoke,
                isEnabled = state.hasConnection,
            ) { onAction(DetailActions.DownloadQuote()) }
            IconCard(
                cDescription = "Shown",
                icon = Icons.Outlined.RemoveRedEye,
                colorIcon = AppColorSchema.whiteSmoke,
                isEnabled = state.hasConnection,
                valueStatistic = statistics.showns
            ) { }
        }
    }
}