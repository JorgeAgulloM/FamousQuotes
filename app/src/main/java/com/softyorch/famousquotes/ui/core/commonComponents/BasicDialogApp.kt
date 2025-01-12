package com.softyorch.famousquotes.ui.core.commonComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.softyorch.famousquotes.ui.screens.home.components.AppIcon
import com.softyorch.famousquotes.ui.screens.home.components.ButtonApp
import com.softyorch.famousquotes.ui.screens.home.components.HeaderSubtitleApp
import com.softyorch.famousquotes.ui.screens.home.components.SpacerHeight
import com.softyorch.famousquotes.ui.screens.home.components.SpacerWidth
import com.softyorch.famousquotes.ui.screens.home.components.TextDescription
import com.softyorch.famousquotes.ui.screens.home.components.TextInfo
import com.softyorch.famousquotes.ui.theme.AppColorSchema
import com.softyorch.famousquotes.ui.utils.DialogCloseAction
import com.softyorch.famousquotes.ui.utils.DialogCloseAction.DISMISS
import com.softyorch.famousquotes.ui.utils.DialogCloseAction.POSITIVE
import com.softyorch.famousquotes.ui.utils.DialogCloseAction.NEGATIVE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicDialogApp(
    text: String,
    auxText: String? = null,
    title: String? = null,
    textBtnNegative: String? = null,
    textBtnPositive: String? = null,
    blockDismissActions: Boolean = false,
    onActions: (DialogCloseAction) -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = { onActions(if (blockDismissActions) DISMISS else POSITIVE) },
        modifier = Modifier.background(
            color = AppColorSchema.background,
            shape = MaterialTheme.shapes.large
        ),
        properties = DialogProperties(
            dismissOnBackPress = blockDismissActions,
            dismissOnClickOutside = blockDismissActions
        )
    ) {
        ContentDialog(title, text, auxText, textBtnNegative, textBtnPositive, onActions)
    }
}

@Composable
private fun ContentDialog(
    title: String?,
    text: String,
    auxText: String?,
    textBtnNegative: String?,
    textBtnPositive: String?,
    onActions: (DialogCloseAction) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        AppIcon()
        HeaderSubtitleApp()
        SpacerHeight(height = 32)

        title?.let {
            TextInfo(it)
            SpacerHeight()
        }

        TextDescription(text)
        SpacerHeight()

        auxText?.let {
            TextDescription(it)
            SpacerHeight()
        }

        val rowOrColumn = rowOrColumnCalculate(textBtnNegative, textBtnPositive)

        if (rowOrColumn == RowOrColumn.Column) ButtonsInColumn(
            textBtnPositive = textBtnPositive,
            onActions = onActions,
            textBtnNegative = textBtnNegative
        ) else ButtonsInRow(
            textBtnNegative = textBtnNegative,
            onActions = onActions,
            textBtnPositive = textBtnPositive
        )
    }
}

@Composable
private fun rowOrColumnCalculate(textBtnNegative: String?, textBtnPositive: String?) =
    if (
        (!textBtnNegative.isNullOrEmpty() && textBtnNegative.length > 8) ||
        (!textBtnPositive.isNullOrEmpty() && textBtnPositive.length > 8)
    ) RowOrColumn.Column
    else RowOrColumn.Row

@Composable
private fun ButtonsInColumn(
    textBtnPositive: String?,
    onActions: (DialogCloseAction) -> Unit,
    textBtnNegative: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        val modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
        if (textBtnPositive != null) ButtonApp(
            modifier = modifier,
            text = textBtnPositive,
            primary = true
        ) {
            onActions(POSITIVE)
        }
        SpacerHeight(4)
        if (textBtnNegative != null) ButtonApp(
            modifier = modifier,
            text = textBtnNegative
        ) {
            onActions(NEGATIVE)
        }
    }
}

@Composable
private fun ButtonsInRow(
    textBtnNegative: String?,
    onActions: (DialogCloseAction) -> Unit,
    textBtnPositive: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (textBtnNegative != null) ButtonApp(text = textBtnNegative) {
            onActions(NEGATIVE)
        }
        SpacerWidth(4)
        if (textBtnPositive != null) ButtonApp(text = textBtnPositive, primary = true) {
            onActions(POSITIVE)
        }
    }
}

sealed interface RowOrColumn {
    data object Row : RowOrColumn
    data object Column : RowOrColumn
}