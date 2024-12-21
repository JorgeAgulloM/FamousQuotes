package com.softyorch.famousquotes.ui.screens.grid

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softyorch.famousquotes.ui.admob.Banner
import com.softyorch.famousquotes.ui.screens.grid.components.CardItem
import com.softyorch.famousquotes.ui.screens.grid.components.TopBarGrid
import com.softyorch.famousquotes.ui.screens.home.components.SpacerHeight
import com.softyorch.famousquotes.ui.theme.BackgroundColor

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun GridScreen(
    viewModel: GridViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigateToDetail: (String) -> Unit,
    onNavigateBack: () -> Unit
) {

    val paddingTop = with(LocalDensity.current) {
        androidx.compose.foundation.layout.WindowInsets.statusBars.getTop(this).toDp()
    }

    val allQuotes by viewModel.quotes.collectAsStateWithLifecycle()
    val selectedQuotes by viewModel.filterQuotesSelected.collectAsStateWithLifecycle()
    val state: GridState by viewModel.state.collectAsStateWithLifecycle()

    val gridState = rememberLazyGridState()

    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        viewModel.setAction(GridActions.LoadingQuotes())
    }

    Scaffold(
        topBar = {
            TopBarGrid(
                paddingTop = paddingTop,
                filterQuotes = selectedQuotes,
                expanded = state.orderByAscending,
                navigateBack = onNavigateBack,
                onActions = {
                    expanded = !expanded
                    viewModel.setAction(
                        if (state.orderByAscending) GridActions.DescendingOrder() else GridActions.AscendingOrder()
                    )
                }
            ) {
                viewModel.selectFilterQuotes(it)
            }
        },
        containerColor = BackgroundColor
    ) { paddingValues ->

        AnimatedContent(state.isLoading, label = "Animated content grid") { isLoading ->
            if (isLoading) Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            } else LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                columns = GridCells.Fixed(2),
                state = gridState,
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {

                val quoteList = if (state.orderByAscending)
                    allQuotes.sortedBy { it.id }
                 else
                    allQuotes.sortedByDescending { it.id }

                items(quoteList) { quote ->
                    CardItem(
                        item = quote,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    ) { idQuote -> onNavigateToDetail(idQuote) }
                }

                item {
                    SpacerHeight(Banner.heightBanner)
                }
            }
        }
    }
}
