package com.softyorch.famousquotes.ui.screens.detail.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.core.commonComponents.LoadingCircle
import com.softyorch.famousquotes.ui.screens.detail.DetailActions
import com.softyorch.famousquotes.ui.screens.detail.ShareAs
import com.softyorch.famousquotes.ui.screens.detail.model.DetailState
import com.softyorch.famousquotes.ui.core.commonComponents.BasicDialogApp
import com.softyorch.famousquotes.ui.screens.home.components.NoConnectionDialog
import com.softyorch.famousquotes.ui.utils.DialogCloseAction

@Composable
fun SetDialogs(state: DetailState, onActions: (DetailActions) -> Unit) {
    if (state.showDialogNoConnection) NoConnectionDialog {
        onActions(DetailActions.ShowNoConnectionDialog())
    }

    if (state.isLoading) LoadingCircle()

    if (state.shareAs) BasicDialogApp(
        text = stringResource(R.string.dialog_how_do_you_share),
        title = stringResource(R.string.dialog_share_title),
        textBtnPositive = stringResource(R.string.dialog_share_by_text),
        textBtnNegative = stringResource(R.string.dialog_share_by_image),
        blockDismissActions = true
    ) {
        when (it) {
            DialogCloseAction.POSITIVE -> onActions(DetailActions.ShareQuoteAs(shareAs = ShareAs.Text))
            DialogCloseAction.NEGATIVE -> onActions(DetailActions.ShareQuoteAs(shareAs = ShareAs.Image))
            DialogCloseAction.DISMISS -> onActions(DetailActions.HowToShareQuote())
        }
    }

}