package com.softyorch.famousquotes.utils

class TestUtils {
    init {
        setTestMode()
    }

    private fun setTestMode() {
        IsTestMode.isTest = true
    }
}