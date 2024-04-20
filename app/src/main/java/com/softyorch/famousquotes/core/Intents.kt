package com.softyorch.famousquotes.core

import android.content.Context
import android.content.Intent
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class Intents @Inject constructor(@ApplicationContext private val context: Context) {

    suspend fun goToWebShopImages() {
        coroutineScope {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(URL_SHOP_IMAGES)
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    suspend fun goToSearchOwnerInBrowser(owner: String) {
        coroutineScope {
            val intent = Intent(
                Intent.ACTION_WEB_SEARCH
            ).apply {
                putExtra("query", owner)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }
}
