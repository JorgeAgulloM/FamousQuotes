package com.softyorch.famousquotes.ui.screens.grid.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.core.FilterQuotes
import com.softyorch.famousquotes.ui.core.commonComponents.AnimatedIconRotating180Degrees
import com.softyorch.famousquotes.ui.core.commonComponents.AnimatedType
import com.softyorch.famousquotes.ui.core.commonComponents.IconButtonMenu
import com.softyorch.famousquotes.ui.theme.AppColorSchema

@Composable
fun TopBarGrid(
    paddingTop: Dp,
    leftHanded: Boolean,
    filterQuotes: FilterQuotes,
    expanded: Boolean,
    navigateBack: () -> Unit,
    onActions: () -> Unit,
    onClickListener: (FilterQuotes) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, paddingTop, end = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Top
    ) {
        if (leftHanded) IconButtonMenu(
            cDescription = stringResource(R.string.default_text_cont_desc_back),
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            colorIcon = AppColorSchema.iconColor
        ) { navigateBack() } else AnimatedIconRotating180Degrees(
            expanded = expanded,
            iconFirst = Icons.AutoMirrored.Filled.Sort,
            iconSecond = Icons.AutoMirrored.Filled.Sort,
            colorIcon = AppColorSchema.iconColor,
            shadowOn = false,
            animatedType = AnimatedType.Flip,
            onCloseMenu = onActions
        )

        Row(
            modifier = Modifier
                .background(color = AppColorSchema.smoke, shape = MaterialTheme.shapes.large)
                .padding(start = 1.dp, end = 1.dp, top = 2.dp, bottom = 2.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ButtonTopBar(
                contentDescription = stringResource(R.string.default_text_cont_desc_likes),
                filterQuotes = FilterQuotes.Likes,
                icon = Icons.Default.Favorite,
                isSelected = filterQuotes == FilterQuotes.Likes
            ) { onClickListener(it) }
            ButtonTopBar(
                contentDescription = stringResource(R.string.default_text_cont_desc_shown),
                filterQuotes = FilterQuotes.Shown,
                icon = Icons.Default.RemoveRedEye,
                isSelected = filterQuotes == FilterQuotes.Shown,
            ) { onClickListener(it) }
            ButtonTopBar(
                contentDescription = stringResource(R.string.default_text_cont_desc_favorites),
                filterQuotes = FilterQuotes.Favorites,
                icon = Icons.Default.Star,
                isSelected = filterQuotes == FilterQuotes.Favorites
            ) { onClickListener(it) }
        }

        if (leftHanded) AnimatedIconRotating180Degrees(
            expanded = expanded,
            iconFirst = Icons.AutoMirrored.Filled.Sort,
            iconSecond = Icons.AutoMirrored.Filled.Sort,
            colorIcon = AppColorSchema.iconColor,
            shadowOn = false,
            animatedType = AnimatedType.Flip,
            onCloseMenu = onActions
        ) else IconButtonMenu(
            cDescription = stringResource(R.string.default_text_cont_desc_back),
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            colorIcon = AppColorSchema.iconColor
        ) { navigateBack() }
    }
}
