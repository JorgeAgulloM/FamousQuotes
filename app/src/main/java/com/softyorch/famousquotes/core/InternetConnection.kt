/*
 * Copyright (c) 2023-2024. File developed for DailyElectricCost App by Jorge Agulló Martín for SoftYorch
 */

package com.softyorch.famousquotes.core

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InternetConnection @Inject constructor(@ApplicationContext private val context: Context) {

    private var isConnected: Boolean = false

    fun isConnectedFlow(): Flow<Boolean> = startConnectionCallback()

    private fun startConnectionCallback(): Flow<Boolean> = callbackFlow {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                isConnected = true
                channel.trySendBlocking(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                isConnected = false
                channel.trySendBlocking(false)
            }
        }

        connectivityManager.registerDefaultNetworkCallback(networkCallback)
        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }
}
