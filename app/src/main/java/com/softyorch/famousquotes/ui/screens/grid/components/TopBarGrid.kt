package com.softyorch.famousquotes.ui.screens.grid.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.core.FilterQuotes
import com.softyorch.famousquotes.ui.core.commonComponents.IconButtonMenu
import com.softyorch.famousquotes.ui.core.commonComponents.SpacerIconButton
import com.softyorch.famousquotes.ui.theme.WhiteSmoke

@Composable
fun TopBarGrid(
    paddingTop: Dp,
    filterQuotes: FilterQuotes,
    navigateBack: () -> Unit,
    onClickListener: (FilterQuotes) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, paddingTop + 8.dp, end = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        IconButtonMenu(
            cDescription = "Back",
            icon = Icons.AutoMirrored.Filled.ArrowBack
        ) { navigateBack() }

        Row(
            modifier = Modifier
                .background(color = WhiteSmoke, shape = MaterialTheme.shapes.large)
                .padding(start = 1.dp, end = 1.dp, top = 2.dp, bottom = 2.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ButtonTopBar(
                contentDescription = "Likes",
                filterQuotes = FilterQuotes.Likes,
                icon = Icons.Default.Favorite,
                isSelected = filterQuotes == FilterQuotes.Likes
            ) { onClickListener(it) }
            ButtonTopBar(
                contentDescription = "Shown",
                filterQuotes = FilterQuotes.Shown,
                icon = Icons.Default.RemoveRedEye,
                isSelected = filterQuotes == FilterQuotes.Shown,
            ) { onClickListener(it) }
            ButtonTopBar(
                contentDescription = "Favorites",
                filterQuotes = FilterQuotes.Favorites,
                icon = Icons.Default.Star,
                isSelected = filterQuotes == FilterQuotes.Favorites
            ) { onClickListener(it) }
        }
        SpacerIconButton()
    }
}
