package com.softyorch.famousquotes.utils

import com.softyorch.famousquotes.utils.IsTestMode

class TestUtils {
    init {
        setTestMode()
    }

    private fun setTestMode() {
        IsTestMode.isTest = true
    }
}