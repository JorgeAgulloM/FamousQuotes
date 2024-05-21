package com.softyorch.famousquotes.domain.interfaces

import com.softyorch.famousquotes.data.defaultDatabase.DefaultModel

interface IDefaultDatabase {
    fun getDefaultQuote(): DefaultModel
    fun getDefaultImage(): String
}
