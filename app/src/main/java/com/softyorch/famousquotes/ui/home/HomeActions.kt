package com.softyorch.famousquotes.ui.home

import android.net.wifi.hotspot2.pps.HomeSp

sealed class HomeActions {
    data object Info: HomeActions()
    data object New: HomeActions()
    data object Send: HomeActions()
    data object Buy: HomeActions()
}
