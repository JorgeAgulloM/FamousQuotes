package com.softyorch.famousquotes.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.softyorch.famousquotes.data.FakeDataBaseImplement
import com.softyorch.famousquotes.data.network.DatabaseServiceImpl
import com.softyorch.famousquotes.domain.IDatabaseService
import com.softyorch.famousquotes.domain.IFakeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun providesFakeDatabase(): IFakeDatabase = FakeDataBaseImplement()

    @Singleton
    @Provides
    fun providesFirebaseFirestore(): FirebaseFirestore = Firebase.firestore

    @Singleton
    @Provides
    fun providesFirebaseStorage(): FirebaseStorage = Firebase.storage

    @Singleton
    @Provides
    fun providesDatabaseService(firestore: FirebaseFirestore, storage: FirebaseStorage):
            IDatabaseService = DatabaseServiceImpl(firestore, storage)
}
