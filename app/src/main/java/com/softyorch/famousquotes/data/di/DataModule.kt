package com.softyorch.famousquotes.data.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.softyorch.famousquotes.data.datastore.DatastoreImpl
import com.softyorch.famousquotes.data.network.DatabaseServiceImpl
import com.softyorch.famousquotes.data.network.StorageServiceImpl
import com.softyorch.famousquotes.domain.interfaces.IDatabaseService
import com.softyorch.famousquotes.domain.interfaces.IDatastore
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun providesFirebaseFirestore(): FirebaseFirestore = Firebase.firestore

    @Singleton
    @Provides
    fun providesFirebaseStorage(): FirebaseStorage = Firebase.storage

    @Singleton
    @Provides
    fun providesDatabaseService(firestore: FirebaseFirestore):
            IDatabaseService = DatabaseServiceImpl(firestore)

    @Singleton
    @Provides
    fun providesStorageService(storage: FirebaseStorage):
            IStorageService = StorageServiceImpl(storage)

    @Singleton
    @Provides
    fun providesDatastore(@ApplicationContext context: Context):
            IDatastore = DatastoreImpl(context)
}
