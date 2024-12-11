package com.softyorch.famousquotes.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softyorch.famousquotes.domain.model.FamousQuoteModel
import com.softyorch.famousquotes.ui.screens.grid.components.CardItem
import com.softyorch.famousquotes.ui.theme.BackgroundColor

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    id: String,
    viewModel: DetailViewModel,
    onBackNavigation: () -> Unit
) {

    val quote: FamousQuoteModel by viewModel.quote.collectAsStateWithLifecycle()

    LaunchedEffect(true) { viewModel.getQuote(id) }

    Scaffold(modifier = modifier.fillMaxSize(), containerColor = BackgroundColor) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CardItem(quote) { onBackNavigation() }
        }
    }

}