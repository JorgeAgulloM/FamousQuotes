package com.softyorch.famousquotes.data.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.softyorch.famousquotes.core.InternetConnection
import com.softyorch.famousquotes.data.datastore.DatastoreImpl
import com.softyorch.famousquotes.data.defaultDatabase.DefaultDatabaseImpl
import com.softyorch.famousquotes.data.network.AuthServiceImpl
import com.softyorch.famousquotes.data.network.StorageServiceImpl
import com.softyorch.famousquotes.data.network.SubscribeNotificationsByTopicImpl
import com.softyorch.famousquotes.data.network.databaseService.DatabaseListServiceImpl
import com.softyorch.famousquotes.data.network.databaseService.DatabaseQuoteServiceImpl
import com.softyorch.famousquotes.data.network.databaseService.auxFireStore.AuxFireStoreLists
import com.softyorch.famousquotes.data.network.databaseService.auxFireStore.IAuxFireStoreLists
import com.softyorch.famousquotes.domain.interfaces.IAuthService
import com.softyorch.famousquotes.domain.interfaces.IDatabaseListService
import com.softyorch.famousquotes.domain.interfaces.IDatabaseQuoteService
import com.softyorch.famousquotes.domain.interfaces.IDatastore
import com.softyorch.famousquotes.domain.interfaces.IDefaultDatabase
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import com.softyorch.famousquotes.domain.interfaces.ISubscribeNotificationsByTopic
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun providesDatabaseService(
        firestore: FirebaseFirestore
    ): IDatabaseQuoteService = DatabaseQuoteServiceImpl(firestore)

    @Singleton
    @Provides
    fun provideDatabaseListService(
        auxFirebaseLists: IAuxFireStoreLists,
        @ApplicationContext context: Context
    ): IDatabaseListService = DatabaseListServiceImpl(auxFirebaseLists, context)

    @Singleton
    @Provides
    fun providesStorageService(
        storage: FirebaseStorage,
        @ApplicationContext context: Context,
    ): IStorageService = StorageServiceImpl(storage, context)

    @Singleton
    @Provides
    fun providesDatastore(@ApplicationContext context: Context): IDatastore = DatastoreImpl(context)

    @Singleton
    @Provides
    fun providesDefaultDatabase(
        @ApplicationContext context: Context
    ): IDefaultDatabase = DefaultDatabaseImpl(context)

    @Singleton
    @Provides
    fun providesAuthService(auth: FirebaseAuth): IAuthService = AuthServiceImpl(auth)

    @Singleton
    @Provides
    fun providesAuxFirebaseLists(
        firestore: FirebaseFirestore,
        internetConnection: InternetConnection,
        dispatcherDefault: CoroutineDispatcher,
    ): IAuxFireStoreLists = AuxFireStoreLists(firestore, internetConnection, dispatcherDefault)

    @Singleton
    @Provides
    fun provideSubscribeNotificationsByTopic(
        messaging: FirebaseMessaging
    ): ISubscribeNotificationsByTopic = SubscribeNotificationsByTopicImpl(messaging)

}
