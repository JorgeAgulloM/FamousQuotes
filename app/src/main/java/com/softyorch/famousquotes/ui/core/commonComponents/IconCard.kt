package com.softyorch.famousquotes.ui.core.commonComponents

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.ui.theme.BackgroundColor
import com.softyorch.famousquotes.ui.theme.SecondaryColor
import com.softyorch.famousquotes.ui.theme.WhiteSmoke

@Composable
fun IconCard(
    cDescription: String,
    icon: ImageVector,
    secondIcon: ImageVector? = null,
    color: Color = SecondaryColor,
    colorIcon: Color = WhiteSmoke,
    valueStatistic: Int = -1,
    isVisible: Boolean = true,
    isEnabled: Boolean = true,
    isSelected: Boolean = false,
    onClick: () -> Unit,
) {
    if (isVisible) {
        val rowShape = MaterialTheme.shapes.large
        val colorBackground = selectColor(isEnabled, isSelected, color, colorIcon)

        Box(modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 4.dp)) {
            Row(
                modifier = Modifier
                    .background(color = colorBackground, shape = rowShape)
                    .padding(horizontal = 10.dp, vertical = 2.dp)
                    .clip(rowShape)
                    .clickable { if (isEnabled) onClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedContent(
                    isSelected && secondIcon != null,
                    label = "Animated Content"
                ) { selected ->
                    if (selected)
                        Box(
                            modifier = Modifier.background(color = color, shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = secondIcon!!,
                                contentDescription = cDescription,
                                tint = WhiteSmoke,
                                modifier = Modifier
                                    .padding(2.dp)
                                    .size(16.dp),
                            )
                        }
                    else Icon(
                        imageVector = icon,
                        contentDescription = cDescription,
                        modifier = Modifier
                            .size(20.dp),
                        tint = colorIcon
                    )
                }
                if (valueStatistic >= 0) Text(
                    text = valueStatistic.toString(),
                    color = WhiteSmoke,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

        }
    }
}

@Composable
private fun selectColor(
    isEnabled: Boolean,
    isSelected: Boolean,
    color: Color,
    colorIcon: Color
) = when {
    !isEnabled -> Color.Gray
    isSelected -> color.copy(alpha = 0.2f)
    colorIcon != WhiteSmoke -> colorIcon.copy(alpha = 0.1f)
    else -> WhiteSmoke.copy(alpha = 0.1f)
}

@Preview(showBackground = true)
@Composable
fun IconCardPrev(modifier: Modifier = Modifier) {
    Box(modifier = Modifier.background(color = BackgroundColor)) {
        IconCard(
            cDescription = "Content Description",
            icon = Icons.Rounded.FavoriteBorder,
            secondIcon = Icons.Rounded.Favorite,
            color = Color.Red,
            colorIcon = WhiteSmoke,
            valueStatistic = 23,
            isSelected = false
        ) { }
    }
}

@Preview(showBackground = true)
@Composable
fun IconCardPrevSelected(modifier: Modifier = Modifier) {
    Box(modifier = Modifier.background(color = BackgroundColor)) {
        IconCard(
            cDescription = "Content Description",
            icon = Icons.Rounded.FavoriteBorder,
            secondIcon = Icons.Rounded.Favorite,
            color = Color.Red,
            colorIcon = WhiteSmoke,
            valueStatistic = 2,
            isSelected = true
        ) { }
    }
}

@Preview(showBackground = true)
@Composable
fun IconCardFavoritePrev(modifier: Modifier = Modifier) {
    Box(modifier = Modifier.background(color = BackgroundColor)) {
        IconCard(
            cDescription = "Content Description",
            icon = Icons.Rounded.StarBorder,
            secondIcon = Icons.Rounded.Star,
            color = Color.Yellow,
            colorIcon = WhiteSmoke,
            valueStatistic = 23,
            isSelected = false
        ) { }
    }
}

@Preview(showBackground = true)
@Composable
fun IconCardPrevFavoriteSelected(modifier: Modifier = Modifier) {
    Box(modifier = Modifier.background(color = BackgroundColor)) {
        IconCard(
            cDescription = "Content Description",
            icon = Icons.Rounded.StarBorder,
            secondIcon = Icons.Rounded.Star,
            color = Color.Yellow,
            colorIcon = WhiteSmoke,
            valueStatistic = 23,
            isSelected = true
        ) { }
    }
}

@Preview(showBackground = true)
@Composable
fun IconCardSharePrev(modifier: Modifier = Modifier) {
    Box(modifier = Modifier.background(color = BackgroundColor)) {
        IconCard(
            cDescription = "Content Description",
            icon = Icons.Rounded.Share,
            color = Color.Yellow,
            colorIcon = WhiteSmoke,
            isSelected = false
        ) { }
    }
}
