package com.softyorch.famousquotes.ui.screens.grid

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softyorch.famousquotes.ui.admob.Banner
import com.softyorch.famousquotes.ui.screens.grid.components.CardItem
import com.softyorch.famousquotes.ui.screens.grid.components.TopBarGrid
import com.softyorch.famousquotes.ui.screens.home.components.SpacerHeight
import com.softyorch.famousquotes.ui.theme.BackgroundColor

@Composable
fun GridScreen(viewModel: GridViewModel, onNavigateToDetail: (String) -> Unit, onNavigateBack: () -> Unit) {

    val paddingTop = with(LocalDensity.current) {
        androidx.compose.foundation.layout.WindowInsets.statusBars.getTop(this).toDp()
    }

    val allQuotes by viewModel.quotes.collectAsStateWithLifecycle()
    val selectedQuotes by viewModel.filterQuotesSelected.collectAsStateWithLifecycle()

    val gridState = rememberLazyGridState()

    Scaffold(
        topBar = {
            TopBarGrid(
                paddingTop = paddingTop,
                filterQuotes = selectedQuotes,
                navigateBack = onNavigateBack
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
                    CardItem(item = quote) { idQuote -> onNavigateToDetail(idQuote) }
                }
            }

            item {
                SpacerHeight(Banner.heightBanner)
            }
        }
    }
}
