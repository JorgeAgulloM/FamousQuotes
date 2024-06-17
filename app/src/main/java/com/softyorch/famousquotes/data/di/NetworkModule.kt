package com.softyorch.famousquotes.data.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestore.setLoggingEnabled
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.data.network.BillingServiceImpl
import com.softyorch.famousquotes.domain.interfaces.IBilling
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun providesFirebaseFirestore(): FirebaseFirestore = Firebase.firestore.apply {
        setLoggingEnabled(true)
    }

    @Singleton
    @Provides
    fun providesFirebaseStorage(): FirebaseStorage = Firebase.storage

    @Singleton
    @Provides
    fun providesRemoteConfig(): FirebaseRemoteConfig = Firebase.remoteConfig.apply {
        setConfigSettingsAsync(
            remoteConfigSettings { minimumFetchIntervalInSeconds = 3600 }
        )
        setDefaultsAsync(R.xml.remote_config_defaults)
        fetchAndActivate()
    }

    @Singleton
    @Provides
    fun providesFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun providesBillingService(@ApplicationContext context: Context): IBilling = BillingServiceImpl(context)
}
