package com.softyorch.famousquotes.ui.admob

sealed class BonifiedAdState {
    data object Loading : BonifiedAdState()
    data object Showed : BonifiedAdState()
    data object OnDismissed : BonifiedAdState()
    data object Reward : BonifiedAdState()
    data object Impression : BonifiedAdState()
    data object Clicked : BonifiedAdState()
    data object Close : BonifiedAdState()
    data object Error : BonifiedAdState()
}
