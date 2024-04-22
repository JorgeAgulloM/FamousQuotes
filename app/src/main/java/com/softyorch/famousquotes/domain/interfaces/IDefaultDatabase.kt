package com.softyorch.famousquotes.domain.interfaces

import com.softyorch.famousquotes.data.defaultDatabase.DefaultModel

interface IDefaultDatabase {
    fun getDefaultDatabase(): List<DefaultModel>
}
