package com.softyorch.famousquotes.ui.screens.grid

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.core.FilterQuotes
import com.softyorch.famousquotes.domain.model.FamousQuoteModel
import com.softyorch.famousquotes.ui.core.commonComponents.IconButtonMenu
import com.softyorch.famousquotes.ui.mainActivity.MainActivity
import com.softyorch.famousquotes.ui.theme.BackgroundColor

@Composable
fun GridScreen(viewModel: GridViewModel, navigateBack: () -> Unit) {

    val paddingTop = MainActivity.paddingTop

    val allQuotes by viewModel.quotes.collectAsStateWithLifecycle()
    val selectedQuotes by viewModel.filterQuotesSelected.collectAsStateWithLifecycle()

    val gridState = rememberLazyGridState()

    Scaffold(
        topBar = {
            TopBarGrid(
                paddingTop = paddingTop,
                filterQuotes = selectedQuotes,
                navigateBack = navigateBack,
                onClickSettingsListener = {}
            ) {
                viewModel.selectFilterQuotes(it)
            }
        },
        containerColor = BackgroundColor
    ) { paddingValues ->

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            columns = GridCells.Fixed(2),
            state = gridState,
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            allQuotes?.let {
                items(it) { quote ->
                    CardItem(item = quote)
                }
            }
        }
    }
}

@Composable
fun CardItem(item: FamousQuoteModel?) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .padding(start = 4.dp, end = 4.dp, bottom = 16.dp)
            .fillMaxWidth()
            .height(240.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (item == null || item.imageUrl.isEmpty()) {
                CircularProgressIndicator()
            } else {
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(context)
                        .data(item.imageUrl)
                        .crossfade(true) // Set the target size to load the image at.
                        .build(),
                    contentScale = ContentScale.Fit
                )

                val painterState = painter.state

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Card(
                        modifier = Modifier
                            .height(180.dp)
                            .padding(4.dp),
                        shape = MaterialTheme.shapes.large,
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = stringResource(R.string.main_content_desc_image),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Text(
                        text = item.body,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(8.dp),
                    )
                }

                if (painterState !is AsyncImagePainter.State.Success) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun TopBarGrid(
    paddingTop: Dp,
    filterQuotes: FilterQuotes,
    navigateBack: () -> Unit,
    onClickSettingsListener: () -> Unit,
    onClickListener: (FilterQuotes) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, paddingTop + 8.dp, end = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButtonMenu(
            cDescription = "Back",
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            shadowOn = true
        ) { navigateBack() }

        Row(
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
                filterQuotes = FilterQuotes.Seen,
                icon = Icons.Default.RemoveRedEye,
                isSelected = filterQuotes == FilterQuotes.Seen
            ) { onClickListener(it) }
            ButtonTopBar(
                contentDescription = "Favorites",
                filterQuotes = FilterQuotes.Favorites,
                icon = Icons.Default.Star,
                isSelected = filterQuotes == FilterQuotes.Favorites
            ) { onClickListener(it) }
            IconButtonMenu(
                cDescription = "Back",
                icon = Icons.Default.Settings,
                shadowOn = true
            ) { onClickSettingsListener() }
        }
    }
}

@Composable
private fun ButtonTopBar(
    contentDescription: String,
    filterQuotes: FilterQuotes,
    icon: ImageVector,
    isSelected: Boolean,
    onClickListener: (FilterQuotes) -> Unit
) {
    IconButtonMenu(
        cDescription = contentDescription,
        icon = icon,
        shadowOn = true,
        isSelected = isSelected
    ) { onClickListener(filterQuotes) }
}

