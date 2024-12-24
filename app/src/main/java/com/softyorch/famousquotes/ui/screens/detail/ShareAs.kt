package com.softyorch.famousquotes.ui.screens.detail

sealed interface ShareAs {
    data object Text: ShareAs
    data object Image: ShareAs
}
